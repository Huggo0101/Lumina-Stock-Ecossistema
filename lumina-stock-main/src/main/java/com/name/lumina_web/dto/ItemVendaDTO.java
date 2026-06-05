package com.name.lumina_web.dto;

public class ItemVendaDTO {
    private Long id;
    private int quantidade;

    // Getters e Setters (Obrigatórios)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}