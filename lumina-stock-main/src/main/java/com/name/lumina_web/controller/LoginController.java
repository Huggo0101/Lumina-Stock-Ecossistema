package com.name.lumina_web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.name.lumina_web.model.Usuario;
import com.name.lumina_web.repository.UsuarioRepository;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Instancia o cliente HTTP para comunicação entre os microserviços
    private final RestTemplate restTemplate = new RestTemplate();
    private final String AUTH_SERVICE_URL = "http://localhost:8083/api/auth";

    // ==========================================
    // ROTAS DE LOGIN E VERIFICAÇÃO 2FA
    // ==========================================

    @GetMapping("/login")
    public String telaLogin() {
        return "login";
    }

    @PostMapping("/logar")
    public String logar(@RequestParam String usuario, @RequestParam String senha, HttpSession session, RedirectAttributes atributos) {
        
        Usuario user = usuarioRepository.findByUsuarioAndSenha(usuario, senha);
        
        if (user != null) {
            // Verifica se o 2FA está ativo para este usuário
            if (user.isIs2faEnabled()) {
                session.setAttribute("usuarioPendente2FA", user);
                return "redirect:/verificar-2fa";
            }
            
            session.setAttribute("usuarioLogado", user);
            return "redirect:/menu";
        } else {
            atributos.addFlashAttribute("erro", "Usuário ou senha inválidos!");
            return "redirect:/login";
        }
    }

    @GetMapping("/verificar-2fa")
    public String tela2FA() {
        return "verificar-2fa";
    }

    @PostMapping("/validar-2fa")
    public String validar2FA(@RequestParam String codigo, HttpSession session, RedirectAttributes atributos) {
        Usuario user = (Usuario) session.getAttribute("usuarioPendente2FA");
        
        if (user != null) {
            // Faz uma requisição POST externa para o microserviço validar o token matemático
            String url = AUTH_SERVICE_URL + "/validar?secret=" + user.getSecretKey() + "&codigo=" + codigo;
            Boolean isCodigoValido = restTemplate.postForObject(url, null, Boolean.class);
            
            if (Boolean.TRUE.equals(isCodigoValido)) {
                session.setAttribute("usuarioLogado", user);
                session.removeAttribute("usuarioPendente2FA");
                return "redirect:/menu";
            }
        }
        
        atributos.addFlashAttribute("erro", "Código de autenticação inválido!");
        return "redirect:/verificar-2fa";
    }

    // ==========================================
    // ROTAS DE CONFIGURAÇÃO DO 2FA (QR CODE)
    // ==========================================

    @GetMapping("/configurar-2fa")
    public String telaConfigurar2FA(HttpSession session, Model model) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogado");
        if (user == null) return "redirect:/login";

        try {
            // Consome do microserviço a geração de uma nova chave secreta semente
            String secret = restTemplate.getForObject(AUTH_SERVICE_URL + "/gerar-segredo", String.class);
            session.setAttribute("tempSecret", secret); 
            
            // Consome do microserviço a string Data URI do QR Code correspondente
            String urlQr = AUTH_SERVICE_URL + "/gerar-qr?secret=" + secret + "&usuario=" + user.getUsuario();
            String qrCodeDataUri = restTemplate.getForObject(urlQr, String.class);
            
            model.addAttribute("qrCode", qrCodeDataUri);
        } catch (Exception e) {
            model.addAttribute("erro", "O microsserviço de autenticação está fora do ar.");
            return "menu";
        }
        
        return "configurar-2fa";
    }

    @PostMapping("/ativar-2fa")
    public String ativar2FA(@RequestParam String codigo, HttpSession session, RedirectAttributes atributos) {
        Usuario user = (Usuario) session.getAttribute("usuarioLogado");
        String secret = (String) session.getAttribute("tempSecret");

        if (user != null && secret != null) {
            // Envia o token inserido para validação remota na porta 8083
            String url = AUTH_SERVICE_URL + "/validar?secret=" + secret + "&codigo=" + codigo;
            Boolean isCodigoValido = restTemplate.postForObject(url, null, Boolean.class);
            
            if (Boolean.TRUE.equals(isCodigoValido)) {
                user.setSecretKey(secret);
                user.setIs2faEnabled(true);
                usuarioRepository.save(user);
                session.removeAttribute("tempSecret");
                
                atributos.addFlashAttribute("mensagem", "Autenticação de Duplo Fator (NFA) ativada com sucesso!");
                return "redirect:/menu";
            }
        }

        atributos.addFlashAttribute("erro", "Código inválido. Tente novamente.");
        return "redirect:/configurar-2fa";
    }

    // ==========================================
    // ROTAS DE REGISTRO E LOGOUT
    // ==========================================

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/registrar")
    public String telaRegistro() {
        return "registro";
    }

    @PostMapping("/salvar-usuario")
    public String salvarUsuario(@ModelAttribute("conta") Usuario novoUsuario, Model model) {
        
        Usuario existente = usuarioRepository.findByUsuario(novoUsuario.getUsuario());
        
        if (existente != null) {
            model.addAttribute("erro", "Este login já está em uso. Escolha outro.");
            return "registro";
        }
        
        usuarioRepository.save(novoUsuario);
        model.addAttribute("sucesso", "Conta criada com sucesso! Faça seu login.");
        return "login";
    }
}