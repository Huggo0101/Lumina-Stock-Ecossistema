package com.name.lumina_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.name.lumina_web.model.ItemVenda;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {
    
    // Mantido intacto: Calcula o total histórico geral (usado para o Dashboard/Estoque)
    @Query("SELECT COALESCE(SUM(i.quantidade), 0) FROM ItemVenda i WHERE i.produto.id = :produtoId")
    int sumQuantidadeByProdutoId(@Param("produtoId") Long produtoId);

    // NOVA CONSULTA: Calcula o total cruzando com a data da venda (usado para os Relatórios)
    @Query("SELECT COALESCE(SUM(i.quantidade), 0) FROM ItemVenda i WHERE i.produto.id = :produtoId AND YEAR(i.venda.dataVenda) = :ano AND MONTH(i.venda.dataVenda) = :mes")
    int sumQuantidadeByProdutoIdAndMesAno(@Param("produtoId") Long produtoId, @Param("mes") int mes, @Param("ano") int ano);
}