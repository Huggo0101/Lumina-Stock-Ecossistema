package com.name.lumina_web.controller;

import com.name.lumina_web.model.Produto;
import com.name.lumina_web.repository.ItemVendaRepository;
import com.name.lumina_web.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dados")
public class ProdutoRestController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemVendaRepository itemVendaRepository;

    // Esta rota fornecerá os dados brutos pela rede para o microserviço de Lucros
    @GetMapping("/produtos-ativos")
    public List<Produto> obterDadosParaCalculo() {
        List<Produto> produtos = produtoRepository.findByAtivoTrue();
        
        for (Produto p : produtos) {
            int totalVendido = itemVendaRepository.sumQuantidadeByProdutoId(p.getId());
            p.setQuantidadeVendida(totalVendido);
        }
        return produtos;
    }
}