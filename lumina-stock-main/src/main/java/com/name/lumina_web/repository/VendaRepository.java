package com.name.lumina_web.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.name.lumina_web.model.Venda;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {}