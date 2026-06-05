package com.duplofatornfa.lumina_auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duplofatornfa.lumina_auth.service.AutenticacaoService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @GetMapping("/gerar-segredo")
    public String gerarSegredo() {
        return autenticacaoService.gerarSecretKey();
    }

    @GetMapping("/gerar-qr")
    public String gerarQrCode(@RequestParam String secret, @RequestParam String usuario) throws Exception {
        return autenticacaoService.gerarQrCodeDataUri(secret, usuario);
    }

    @PostMapping("/validar")
    public boolean validarCodigo(@RequestParam String secret, @RequestParam String codigo) {
        
        // --- INÍCIO DOS LOGS DE DEPURAÇÃO ---
        System.out.println("\n========== DEBUG LUMINA AUTH (8083) ==========");
        System.out.println("A. Segredo recebido via REST: " + secret);
        System.out.println("B. Codigo recebido via REST: [" + codigo + "]");
        // --- FIM DOS LOGS DE DEPURAÇÃO ---

        boolean resultado = autenticacaoService.validarCodigo(secret, codigo);
        
        System.out.println("C. Resultado da matematica TOTP: " + resultado);
        System.out.println("==============================================\n");
        
        return resultado;
    }
}