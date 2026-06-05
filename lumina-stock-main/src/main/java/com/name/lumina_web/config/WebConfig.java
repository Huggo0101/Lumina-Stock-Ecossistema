package com.name.lumina_web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.name.lumina_web.interceptor.LoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // Protege TODAS as páginas
                .excludePathPatterns(
                        "/",            // Exceto a Landing Page
                        "/login",       // Exceto a tela de Login
                        "/logar",       // Exceto a ação de tentar logar
                        "/registrar",   // Exceto a tela de Registro
                        "/salvar-usuario", // Exceto a ação de salvar o usuário
                        "/verificar-2fa", // Exceto a tela de digitar o PIN do app
                        "/validar-2fa",   // Exceto a ação de validar o PIN
                        "/css/**", 
                        "/js/**",
                        "/images/**",
                        "/api/dados/**"
                );
    }
}