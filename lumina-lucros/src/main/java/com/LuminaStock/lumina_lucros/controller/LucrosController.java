package com.LuminaStock.lumina_lucros.controller;

import com.LuminaStock.lumina_lucros.dto.ProdutoDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LucrosController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String URL_ESTOQUE = "http://localhost:8080/api/dados/produtos-ativos";

    @GetMapping("/")
    public String painelLucros(
            @RequestParam(name = "usuarioId", required = false) Integer usuarioId, 
            @RequestParam(name = "mesAno", required = false) String mesAno,
            Model model) {
        
        if (usuarioId == null) {
            model.addAttribute("erroIntegracao", "Atenção: Identificação de usuário ausente. Retorne ao menu principal e acesse novamente.");
            return "lucros";
        }

        try {
            // Anexando os parâmetros de busca ao endpoint da porta 8080
            String urlComFiltro = URL_ESTOQUE + "?usuarioId=" + usuarioId;
            if (mesAno != null && !mesAno.trim().isEmpty()) {
                urlComFiltro += "&mesAno=" + mesAno;
                model.addAttribute("mesAnoSelecionado", mesAno);
            }

            ProdutoDTO[] produtosArray = restTemplate.getForObject(urlComFiltro, ProdutoDTO[].class);
            List<ProdutoDTO> produtos = Arrays.asList(produtosArray);

            // Filtra os itens com venda 0 para não poluírem o gráfico do mês
            List<ProdutoDTO> produtosOrdenados = produtos.stream()
                    .filter(p -> p.getQuantidadeVendida() > 0)
                    .sorted(Comparator.comparing(ProdutoDTO::getLucroTotalRealizado).reversed())
                    .collect(Collectors.toList());

            double lucroTotalGeral = produtosOrdenados.stream()
                    .mapToDouble(ProdutoDTO::getLucroTotalRealizado)
                    .sum();

            model.addAttribute("produtos", produtosOrdenados);
            model.addAttribute("lucroTotalGeral", lucroTotalGeral);
            model.addAttribute("usuarioIdParam", usuarioId); // Mantém o ID na URL para o botão de limpar filtro
            
        } catch (Exception e) {
            model.addAttribute("erroIntegracao", "Atenção: Não foi possível conectar ao Módulo de Estoque (Porta 8080). Certifique-se de que ele está rodando.");
        }

        return "lucros";
    }
}