package com.name.lumina_web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.name.lumina_web.model.Usuario;

@Component
public class FiltroSegurancaInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        String uri = request.getRequestURI();

        // 1. Libera recursos estáticos do Bootstrap/Icons se houverem
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/images/")) {
            return true;
        }

        // 2. Rotas públicas do sistema (Acessíveis sem estar logado)
        if (uri.equals("/") || uri.equals("/login") || uri.equals("/logar") || uri.equals("/registrar") || uri.equals("/salvar-usuario")) {
            return true;
        }

        // 3. Se o usuário nem sequer está logado, barra e joga para a tela de login
        if (usuarioLogado == null) {
            if (uri.equals("/verificar-2fa") || uri.equals("/validar-2fa")) {
                return true;
            }
            response.sendRedirect("/login");
            return false;
        }

        // 4. Usuário LOGADO, mas com o Duplo Fator (NFA) DESATIVADO
        if (!usuarioLogado.isIs2faEnabled()) {
            // Permite APENAS ficar no Menu, configurar o QR Code no microserviço, ativar ou deslogar
            if (uri.equals("/menu") || uri.equals("/configurar-2fa") || uri.equals("/ativar-2fa") || uri.equals("/logout")) {
                return true;
            }

            // Se tentar forçar a entrada em qualquer outra rota (estoque, cadastros, relatórios), barra e joga pro menu com parâmetro de erro
            response.sendRedirect("/menu?bloqueado=true");
            return false;
        }

        // 5. Se o NFA estiver ativo, libera o acesso irrestrito a todas as rotas
        return true;
    }
}