package com.name.lumina_web.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.name.lumina_web.model.Produto;
import com.name.lumina_web.model.Usuario;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Método que busca apenas os produtos ativos E que pertencem à conta logada
    List<Produto> findByAtivoTrueAndUsuario(Usuario usuario);
}