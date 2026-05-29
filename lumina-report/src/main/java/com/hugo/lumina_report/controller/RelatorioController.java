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

@Controller // Alterado para @Controller para suportar a interface visual do histórico
@RequestMapping("/api/relatorios")
public class RelatorioController {

    @Autowired
    private HistoricoRepository historicoRepository;

    // --- NOVA ROTA: TELA VISUAL DE HISTÓRICO ---
    @GetMapping("/historico")
    public String exibirHistorico(Model model) {
        // Busca todos os registros no banco H2 e envia para a tela
        List<Historico> registros = historicoRepository.findAll();
        model.addAttribute("historicos", registros);
        return "historico";
    }

    // --- ROTAS DE GERAÇÃO DE EXCEL ---
@PostMapping("/vendas")
    @ResponseBody
    public ResponseEntity<byte[]> gerarRelatorioVendas(
            @RequestBody List<ItemRelatorioDTO> dados,
            @RequestHeader(value = "Usuario-Logado", defaultValue = "Usuário Desconhecido") String usuarioLogado) {
        
        System.out.println("--- GERANDO EXCEL DE VENDAS ---");
        historicoRepository.save(new Historico("Relatório de Vendas", usuarioLogado));
        return criarPlanilha(dados, "Relatorio_de_Vendas", true, false);
    }

    @PostMapping("/lucros")
    @ResponseBody
    public ResponseEntity<byte[]> gerarRelatorioLucros(
            @RequestBody List<ItemRelatorioDTO> dados,
            @RequestHeader(value = "Usuario-Logado", defaultValue = "Usuário Desconhecido") String usuarioLogado) {
        
        System.out.println("--- GERANDO EXCEL DE LUCROS ---");
        historicoRepository.save(new Historico("Relatório de Lucros", usuarioLogado));
        return criarPlanilha(dados, "Relatorio_de_Lucros", false, true);
    }

    // --- MOTOR DE CRIAÇÃO DO EXCEL (MANTIDO INTACTO) ---
    private ResponseEntity<byte[]> criarPlanilha(List<ItemRelatorioDTO> dados, String nomePlanilha, boolean mostrarVendas, boolean mostrarLucro) {
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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", nomePlanilha + ".xlsx");

            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            System.out.println("Erro na fábrica de Excel: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}