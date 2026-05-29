package com.LuminaStock.lumina_lucros.controller;

import com.LuminaStock.lumina_lucros.dto.ProdutoDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LucrosController {

    private final RestTemplate restTemplate = new RestTemplate();
    // Endereço exato da API que criamos no projeto do Estoque
    private final String URL_ESTOQUE = "http://localhost:8080/api/dados/produtos-ativos";

    @GetMapping("/")
    public String painelLucros(Model model) {
        try {
            // 1. Faz o GET na porta 8080 e converte o JSON para o nosso DTO
            ProdutoDTO[] produtosArray = restTemplate.getForObject(URL_ESTOQUE, ProdutoDTO[].class);
            List<ProdutoDTO> produtos = Arrays.asList(produtosArray);

            // 2. Ordena a lista focando nos produtos que deram mais lucro
            List<ProdutoDTO> produtosOrdenados = produtos.stream()
                    .sorted(Comparator.comparing(ProdutoDTO::getLucroTotalRealizado).reversed())
                    .collect(Collectors.toList());

            // 3. Calcula a soma total de lucro de toda a loja
            double lucroTotalGeral = produtosOrdenados.stream()
                    .mapToDouble(ProdutoDTO::getLucroTotalRealizado)
                    .sum();

            model.addAttribute("produtos", produtosOrdenados);
            model.addAttribute("lucroTotalGeral", lucroTotalGeral);
            
        } catch (Exception e) {
            // Se o projeto 8080 estiver desligado, o sistema não quebra, apenas avisa.
            model.addAttribute("erroIntegracao", "Atenção: Não foi possível conectar ao Módulo de Estoque (Porta 8080). Certifique-se de que ele está rodando.");
        }

        return "lucros";
    }
}