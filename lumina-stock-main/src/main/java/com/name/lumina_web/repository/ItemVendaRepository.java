package com.name.lumina_web.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.name.lumina_web.model.ItemVenda;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    
    // Calcula dinamicamente o total vendido de um produto específico
    @Query("SELECT COALESCE(SUM(i.quantidade), 0) FROM ItemVenda i WHERE i.produto.id = :produtoId")
    int sumQuantidadeByProdutoId(@Param("produtoId") Long produtoId);
}