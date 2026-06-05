package com.LuminaStock.lumina_lucros.dto;

public class ProdutoDTO {
    
    private Long id;
    private String nome;
    private double precoCusto;
    private double precoVenda;
    private int quantidadeVendida;

    // --- GETTERS E SETTERS BÁSICOS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public double getPrecoCusto() { return precoCusto; }
    public void setPrecoCusto(double precoCusto) { this.precoCusto = precoCusto; }
    
    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }
    
    public int getQuantidadeVendida() { return quantidadeVendida; }
    public void setQuantidadeVendida(int quantidadeVendida) { this.quantidadeVendida = quantidadeVendida; }

    // --- LÓGICA FINANCEIRA (Calculada em Tempo Real) ---
    public double getLucroTotalRealizado() {
        return (this.precoVenda - this.precoCusto) * this.quantidadeVendida;
    }
    
    public double getMargemLucro() {
        if (this.precoVenda == 0) return 0.0;
        // Fórmula clássica de margem de lucro: ((Venda - Custo) / Venda) * 100
        return ((this.precoVenda - this.precoCusto) / this.precoVenda) * 100.0;
    }
}