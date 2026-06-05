package com.hugo.lumina_report.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "historico_geracao")
public class Historico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoRelatorio; 
    private String usuarioResponsavel; 
    private LocalDateTime dataHoraGeracao;

    // Novo campo para armazenar o ficheiro físico na base de dados
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] arquivoExcel;

    public Historico() {
        this.dataHoraGeracao = LocalDateTime.now();
    }

    public Historico(String tipoRelatorio, String usuarioResponsavel, byte[] arquivoExcel) {
        this.tipoRelatorio = tipoRelatorio;
        this.usuarioResponsavel = usuarioResponsavel;
        this.arquivoExcel = arquivoExcel;
        this.dataHoraGeracao = LocalDateTime.now();
    }

    public String getDataHoraFormatada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHoraGeracao.format(formatter);
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoRelatorio() { return tipoRelatorio; }
    public void setTipoRelatorio(String tipoRelatorio) { this.tipoRelatorio = tipoRelatorio; }

    public String getUsuarioResponsavel() { return usuarioResponsavel; }
    public void setUsuarioResponsavel(String usuarioResponsavel) { this.usuarioResponsavel = usuarioResponsavel; }

    public LocalDateTime getDataHoraGeracao() { return dataHoraGeracao; }
    public void setDataHoraGeracao(LocalDateTime dataHoraGeracao) { this.dataHoraGeracao = dataHoraGeracao; }

    public byte[] getArquivoExcel() { return arquivoExcel; }
    public void setArquivoExcel(byte[] arquivoExcel) { this.arquivoExcel = arquivoExcel; }
}