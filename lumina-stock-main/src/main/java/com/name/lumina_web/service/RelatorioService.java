package com.name.lumina_web.service;

import com.name.lumina_web.dto.ItemRelatorioDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RelatorioService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String URL_BASE = "http://localhost:8081/api/relatorios";

    public byte[] solicitarRelatorioVendas(List<ItemRelatorioDTO> dados, String nomeUsuario) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Usuario-Logado", nomeUsuario); // Embutindo o nome aqui!
            
            HttpEntity<List<ItemRelatorioDTO>> request = new HttpEntity<>(dados, headers);
            ResponseEntity<byte[]> resposta = restTemplate.postForEntity(URL_BASE + "/vendas", request, byte[].class);
            return resposta.getBody();
        } catch (Exception e) {
            System.out.println("Erro ao pedir relatório de Vendas: " + e.getMessage());
            return null;
        }
    }

    public byte[] solicitarRelatorioLucros(List<ItemRelatorioDTO> dados, String nomeUsuario) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Usuario-Logado", nomeUsuario); // Embutindo o nome aqui!
            
            HttpEntity<List<ItemRelatorioDTO>> request = new HttpEntity<>(dados, headers);
            ResponseEntity<byte[]> resposta = restTemplate.postForEntity(URL_BASE + "/lucros", request, byte[].class);
            return resposta.getBody();
        } catch (Exception e) {
            System.out.println("Erro ao pedir relatório de Lucros: " + e.getMessage());
            return null;
        }
    }
}