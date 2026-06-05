package com.duplofatornfa.lumina_auth.service;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class AutenticacaoService {

    public String gerarSecretKey() {
        return new DefaultSecretGenerator().generate();
    }

    public String gerarQrCodeDataUri(String secret, String usuario) throws Exception {
        QrData data = new QrData.Builder()
                .label(usuario)
                .issuer("LuminaStock")
                .secret(secret)
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(30)
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();
        byte[] imageData = generator.generate(data);
        return getDataUriForImage(imageData, generator.getImageMimeType());
    }

    public boolean validarCodigo(String secret, String codigo) {
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        
        // Utiliza-se a classe concreta DefaultCodeVerifier para possibilitar o ajuste de configurações avançadas
        DefaultCodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, new SystemTimeProvider());
        
        // Expande a tolerância do algoritmo para 10 períodos, contornando qualquer dessincronização de relógio na infraestrutura
        verifier.setAllowedTimePeriodDiscrepancy(10);
        
        return verifier.isValidCode(secret, codigo);
    }
}