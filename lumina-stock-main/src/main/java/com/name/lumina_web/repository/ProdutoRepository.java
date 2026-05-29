package com.name.lumina_web.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.name.lumina_web.model.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // A query customizada foi removida. A ordenação será feita em memória no Controller.
    
    // Método que busca apenas os produtos que não foram excluídos logicamente
    List<Produto> findByAtivoTrue();
}