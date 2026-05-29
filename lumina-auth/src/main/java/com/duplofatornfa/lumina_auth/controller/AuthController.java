package com.duplofatornfa.lumina_auth.controller;

import com.duplofatornfa.lumina_auth.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        return autenticacaoService.validarCodigo(secret, codigo);
    }
}