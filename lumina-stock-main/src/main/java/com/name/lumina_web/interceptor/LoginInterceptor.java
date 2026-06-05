package com.name.lumina_web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.name.lumina_web.model.Usuario;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        String uri = request.getRequestURI();

        // 1. Verifica se o usuário não está logado
        if (usuarioLogado == null) {
            response.sendRedirect("/login");
            return false;
        }

        // 2. Usuário LOGADO, mas com o Duplo Fator (NFA) DESATIVADO
        if (!usuarioLogado.isIs2faEnabled()) {
            
            // Permite APENAS ficar no Menu, configurar o QR Code, ativar ou sair
            if (uri.equals("/menu") || uri.equals("/configurar-2fa") || uri.equals("/ativar-2fa") || uri.equals("/logout")) {
                return true;
            }

            // Se tentar acessar qualquer outra rota (estoque, novo, etc), bloqueia e volta pro menu com alerta
            response.sendRedirect("/menu?bloqueado=true");
            return false;
        }

        // 3. Usuário logado e com NFA ativo (Acesso total liberado)
        return true;
    }
}