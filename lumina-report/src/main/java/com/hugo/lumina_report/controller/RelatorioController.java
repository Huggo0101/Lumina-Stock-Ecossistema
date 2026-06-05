package com.hugo.lumina_report.controller;

import com.hugo.lumina_report.dto.ItemRelatorioDTO;
import com.hugo.lumina_report.model.Historico;
import com.hugo.lumina_report.repository.HistoricoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Controller 
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private HistoricoRepository historicoRepository;

    @GetMapping("/historico")
    public String exibirHistorico(
            @RequestParam(name = "usuario", required = false) String usuario, 
            @RequestParam(name = "mesAno", required = false) String mesAno,
            Model model) {
        
        List<Historico> registros = historicoRepository.findAll();
        
        // Filtro 1: Isolamento por Usuário
        if (usuario != null && !usuario.trim().isEmpty()) {
            registros = registros.stream()
                    .filter(h -> usuario.equals(h.getUsuarioResponsavel()))
                    .collect(Collectors.toList());
        }

        // Filtro 2: Isolamento por Data (Mês e Ano)
        if (mesAno != null && !mesAno.trim().isEmpty()) {
            String[] partes = mesAno.split("-");
            int anoFiltro = Integer.parseInt(partes[0]);
            int mesFiltro = Integer.parseInt(partes[1]);
            
            registros = registros.stream()
                    .filter(h -> h.getDataHoraGeracao().getYear() == anoFiltro && h.getDataHoraGeracao().getMonthValue() == mesFiltro)
                    .collect(Collectors.toList());
            
            model.addAttribute("mesAnoSelecionado", mesAno);
        }
        
        model.addAttribute("historicos", registros);
        model.addAttribute("usuarioParam", usuario); // Mantém o usuário na URL ao filtrar a data
        return "historico";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> baixarNovamente(@PathVariable Long id) {
        Historico historico = historicoRepository.findById(id).orElse(null);
        
        if (historico != null && historico.getArquivoExcel() != null) {
            String nomeArquivo = historico.getTipoRelatorio().replace(" ", "_") + "_" + id + ".xlsx";
            return formatarRespostaExcel(historico.getArquivoExcel(), nomeArquivo);
        }
        
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/vendas")
    @ResponseBody
    public ResponseEntity<byte[]> gerarRelatorioVendas(
            @RequestBody List<ItemRelatorioDTO> dados,
            @RequestHeader(value = "Usuario-Logado", defaultValue = "Usuário Desconhecido") String usuarioLogado) {
        
        System.out.println("--- GERANDO EXCEL DE VENDAS ---");
        byte[] arquivo = criarPlanilhaBytes(dados, "Relatorio_de_Vendas", true, false);
        
        if (arquivo != null) {
            historicoRepository.save(new Historico("Relatório de Vendas", usuarioLogado, arquivo));
            return formatarRespostaExcel(arquivo, "Relatorio_de_Vendas.xlsx");
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/lucros")
    @ResponseBody
    public ResponseEntity<byte[]> gerarRelatorioLucros(
            @RequestBody List<ItemRelatorioDTO> dados,
            @RequestHeader(value = "Usuario-Logado", defaultValue = "Usuário Desconhecido") String usuarioLogado) {
        
        System.out.println("--- GERANDO EXCEL DE LUCROS ---");
        byte[] arquivo = criarPlanilhaBytes(dados, "Relatorio_de_Lucros", false, true);
        
        if (arquivo != null) {
            historicoRepository.save(new Historico("Relatório de Lucros", usuarioLogado, arquivo));
            return formatarRespostaExcel(arquivo, "Relatorio_de_Lucros.xlsx");
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] criarPlanilhaBytes(List<ItemRelatorioDTO> dados, String nomePlanilha, boolean mostrarVendas, boolean mostrarLucro) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(nomePlanilha);

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Mês");
            headerRow.createCell(1).setCellValue("Produto");
            
            int colIdx = 2;
            if (mostrarVendas) {
                headerRow.createCell(colIdx++).setCellValue("Quantidade Vendida");
            }
            if (mostrarLucro) {
                headerRow.createCell(colIdx++).setCellValue("Lucro Gerado (R$)");
                headerRow.createCell(colIdx++).setCellValue("Margem de Lucro (%)");
            }

            int rowIdx = 1;
            for (ItemRelatorioDTO item : dados) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getMesRef() != null ? item.getMesRef() : "N/D");
                row.createCell(1).setCellValue(item.getNomeProduto() != null ? item.getNomeProduto() : "Produto Desconhecido");
                
                colIdx = 2;
                if (mostrarVendas) {
                    row.createCell(colIdx++).setCellValue(item.getQuantidadeVendida() != null ? item.getQuantidadeVendida() : 0);
                }
                if (mostrarLucro) {
                    row.createCell(colIdx++).setCellValue(item.getLucroGerado() != null ? item.getLucroGerado() : 0.0);
                    row.createCell(colIdx++).setCellValue(item.getMargemLucro() != null ? item.getMargemLucro() : 0.0);
                }
            }

            for(int i = 0; i < colIdx; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            System.out.println("Erro na fábrica de Excel: " + e.getMessage());
            return null;
        }
    }

    private ResponseEntity<byte[]> formatarRespostaExcel(byte[] arquivo, String nomeArquivo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", nomeArquivo);
        return new ResponseEntity<>(arquivo, headers, HttpStatus.OK);
    }
}