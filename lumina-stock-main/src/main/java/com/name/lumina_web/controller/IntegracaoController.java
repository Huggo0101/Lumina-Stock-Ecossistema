package com.name.lumina_web.controller;

import com.name.lumina_web.dto.ItemRelatorioDTO;
import com.name.lumina_web.model.Produto;
import com.name.lumina_web.model.Usuario;
import com.name.lumina_web.repository.ItemVendaRepository;
import com.name.lumina_web.repository.ProdutoRepository;
import com.name.lumina_web.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class IntegracaoController {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemVendaRepository itemVendaRepository;

    @PostMapping("/enviar-relatorio-vendas")
    public ResponseEntity<byte[]> baixarRelatorioVendas(jakarta.servlet.http.HttpSession session) {
        String nomeUsuario = capturarNomeUsuario(session);
        List<ItemRelatorioDTO> pacote = gerarDadosReais(); 
        
        byte[] arquivo = relatorioService.solicitarRelatorioVendas(pacote, nomeUsuario); 
        return formatarDownload(arquivo, "relatorio_vendas.xlsx");
    }

    @PostMapping("/enviar-relatorio-lucros")
    public ResponseEntity<byte[]> baixarRelatorioLucros(jakarta.servlet.http.HttpSession session) {
        String nomeUsuario = capturarNomeUsuario(session);
        List<ItemRelatorioDTO> pacote = gerarDadosReais(); 
        
        byte[] arquivo = relatorioService.solicitarRelatorioLucros(pacote, nomeUsuario); 
        return formatarDownload(arquivo, "relatorio_lucros.xlsx");
    }

    // --- MÉTODOS AUXILIARES ---

    private String capturarNomeUsuario(jakarta.servlet.http.HttpSession session) {
        Object objUsuario = session.getAttribute("usuarioLogado"); 
        if (objUsuario instanceof Usuario) {
            Usuario u = (Usuario) objUsuario;
            return (u.getNome() != null && !u.getNome().trim().isEmpty()) ? u.getNome() : u.getUsuario();
        }
        return "Desconhecido";
    }

    private List<ItemRelatorioDTO> gerarDadosReais() {
        List<ItemRelatorioDTO> pacoteDeDados = new ArrayList<>();
        
        // Busca o histórico de todos os produtos (mesmo os ocultos/excluídos logicamente)
        List<Produto> todosProdutos = produtoRepository.findAll(); 
        
        // Formata o mês/ano atual para exibição no Excel
        String mesAtual = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));

        for (Produto p : todosProdutos) {
            Integer totalVendido = itemVendaRepository.sumQuantidadeByProdutoId(p.getId());
            int qtdVendas = (totalVendido != null) ? totalVendido : 0;

            // Só adiciona ao relatório os produtos que tiveram saída
            if (qtdVendas > 0) {
                p.setQuantidadeVendida(qtdVendas);
                
                ItemRelatorioDTO item = new ItemRelatorioDTO();
                item.setMesRef("Até " + mesAtual); // Como é um histórico total
                item.setNomeProduto(p.getNome());
                item.setQuantidadeVendida(qtdVendas);
                item.setLucroGerado(p.getLucroTotalRealizado());
                
                // Calcula a Margem de Lucro (% do lucro sobre o preço de venda)
                double margem = 0.0;
                if (p.getPrecoVenda() > 0) {
                    margem = ((p.getPrecoVenda() - p.getPrecoCusto()) / p.getPrecoVenda()) * 100.0;
                }
                item.setMargemLucro(margem);

                pacoteDeDados.add(item);
            }
        }
        
        return pacoteDeDados;
    }

    private ResponseEntity<byte[]> formatarDownload(byte[] arquivo, String nomeArquivo) {
        if (arquivo == null) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", nomeArquivo);

        return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
    }
}