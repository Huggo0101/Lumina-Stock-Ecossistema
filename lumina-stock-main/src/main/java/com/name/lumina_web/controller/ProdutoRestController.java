package com.name.lumina_web.controller;

import com.name.lumina_web.model.Produto;
import com.name.lumina_web.model.Usuario;
import com.name.lumina_web.repository.ItemVendaRepository;
import com.name.lumina_web.repository.ProdutoRepository;
import com.name.lumina_web.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dados")
public class ProdutoRestController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ItemVendaRepository itemVendaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/produtos-ativos")
    public List<Produto> obterDadosParaCalculo(
            @RequestParam(name = "usuarioId", required = false) Integer usuarioId,
            @RequestParam(name = "mesAno", required = false) String mesAno) {
        
        if (usuarioId == null) {
            return new ArrayList<>();
        }

        Optional<Usuario> userOpt = usuarioRepository.findById(usuarioId);
        if (userOpt.isEmpty()) {
            return new ArrayList<>();
        }

        List<Produto> produtos = produtoRepository.findByAtivoTrueAndUsuario(userOpt.get());
        
        // Separação do período caso tenha sido enviado pelo Front-End
        Integer ano = null;
        Integer mes = null;
        if (mesAno != null && !mesAno.trim().isEmpty()) {
            String[] partes = mesAno.split("-");
            ano = Integer.parseInt(partes[0]);
            mes = Integer.parseInt(partes[1]);
        }

        for (Produto p : produtos) {
            Integer totalVendido;
            
            // Verifica qual Query utilizar com base no filtro
            if (ano != null && mes != null) {
                totalVendido = itemVendaRepository.sumQuantidadeByProdutoIdAndMesAno(p.getId(), mes, ano);
            } else {
                totalVendido = itemVendaRepository.sumQuantidadeByProdutoId(p.getId());
            }
            
            p.setQuantidadeVendida(totalVendido != null ? totalVendido : 0);
        }
        
        return produtos;
    }
}