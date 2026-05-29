package com.hugo.lumina_report.dto; 
public class ItemRelatorioDTO {
    
    private String mesRef;           
    private String nomeProduto;
    private Integer quantidadeVendida;
    private Double lucroGerado;
    private Double margemLucro;      

    public ItemRelatorioDTO() {}

    // Getters e Setters
    public String getMesRef() { return mesRef; }
    public void setMesRef(String mesRef) { this.mesRef = mesRef; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Integer getQuantidadeVendida() { return quantidadeVendida; }
    public void setQuantidadeVendida(Integer quantidadeVendida) { this.quantidadeVendida = quantidadeVendida; }

    public Double getLucroGerado() { return lucroGerado; }
    public void setLucroGerado(Double lucroGerado) { this.lucroGerado = lucroGerado; }

    public Double getMargemLucro() { return margemLucro; }
    public void setMargemLucro(Double margemLucro) { this.margemLucro = margemLucro; }
}