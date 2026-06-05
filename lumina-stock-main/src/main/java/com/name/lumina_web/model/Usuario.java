package com.name.lumina_web.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String nome;
    private String email;
    private String usuario;
    private String senha; 
    
    // --- NOVOS CAMPOS PARA 2FA ---
    private String secretKey;         // Semente secreta gerada para o QR Code
    private boolean is2faEnabled = false; // Indica se o usuário completou a configuração

    public Usuario() {}

    // --- GETTERS E SETTERS ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public boolean isIs2faEnabled() { return is2faEnabled; }
    public void setIs2faEnabled(boolean is2faEnabled) { this.is2faEnabled = is2faEnabled; }
}