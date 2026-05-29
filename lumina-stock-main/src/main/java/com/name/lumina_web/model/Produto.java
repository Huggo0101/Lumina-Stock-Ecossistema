package com.name.lumina_web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Transient;
import jakarta.persistence.Table;

@Entity 
@Table(name = "produtos") // Boa prática: explicitar o nome da tabela
public class Produto {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    private String nome;
    
    // O Spring Boot mapeia camelCase para snake_case automaticamente (preco_custo)
    private double precoCusto; 
    private double precoVenda;
    private int quantidadeEstoque;
    
    // --- NOVO RELACIONAMENTO COM CATEGORIA ---
    @ManyToOne
    @JoinColumn(name = "categoria_id") // Nome da chave estrangeira no banco
    private Categoria categoria;
    
    // --- CAMPO NÃO PERSISTENTE NO BANCO (@Transient) ---
    @Transient 
    private int quantidadeVendida = 0;

    // --- NOVO CAMPO: EXCLUSÃO LÓGICA ---
    private boolean ativo = true;

    // --- CONSTRUTOR VAZIO ---
    public Produto() {
    }

    // --- GETTERS E SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public double getPrecoCusto() {
        return precoCusto;
    }

    public void setPrecoCusto(double precoCusto) {
        this.precoCusto = precoCusto;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(double precoVenda) {
        this.precoVenda = precoVenda;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getQuantidadeVendida() {
        return quantidadeVendida;
    }

    public void setQuantidadeVendida(int quantidadeVendida) {
        this.quantidadeVendida = quantidadeVendida;
    }

    // Getter e Setter do novo campo
    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
    
    // --- MÉTODOS DE CÁLCULO (Lógica de Negócio) ---
    
    public double getLucroUnitario() {
        return this.precoVenda - this.precoCusto;
    }
    
    public double getLucroTotalRealizado() {
        return (this.precoVenda - this.precoCusto) * this.quantidadeVendida;
    }   
}