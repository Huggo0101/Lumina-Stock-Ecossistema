package com.name.lumina_web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.name.lumina_web.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Método original: busca no banco se existe alguém com este usuário E senha (usado no Login)
    Usuario findByUsuarioAndSenha(String usuario, String senha);
    
    // Método adicionado: busca no banco apenas pelo usuário (usado na validação de cadastro)
    Usuario findByUsuario(String usuario);
}