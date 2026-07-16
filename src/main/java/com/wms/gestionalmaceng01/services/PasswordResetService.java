package com.wms.gestionalmaceng01.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import com.wms.gestionalmaceng01.models.PasswordResetToken;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.PasswordResetTokenRepository;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;

@Service
public class PasswordResetService {

    private static final Logger log =
            LoggerFactory.getLogger(PasswordResetService.class);

    private static final Duration VIGENCIA_TOKEN = Duration.ofMinutes(15);
    private static final int LONGITUD_MINIMA_CLAVE = 8;

    private final PasswordResetTokenRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String baseUrl;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl
    ) {
        this.tokenRepository = tokenRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.baseUrl = baseUrl;
    }

    @Transactional
    public void solicitarRecuperacion(String correo) {
        if (correo == null || correo.isBlank()) {
            return;
        }

        Optional<Usuario> usuarioOpt = usuarioRepository
                .findByCorreoIgnoreCase(correo.trim());

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().isEstado()) {
            log.info(
                    "Solicitud de recuperación ignorada para una cuenta inexistente o inactiva."
            );
            return;
        }

        Usuario usuario = usuarioOpt.get();
        tokenRepository.deleteAllByUsuario(usuario);

        String tokenPlano = generarTokenSeguro();
        LocalDateTime ahora = LocalDateTime.now();

        PasswordResetToken token = new PasswordResetToken();
        token.setUsuario(usuario);
        token.setTokenHash(calcularHash(tokenPlano));
        token.setFechaCreacion(ahora);
        token.setFechaExpiracion(ahora.plus(VIGENCIA_TOKEN));
        token.setUtilizado(false);
        tokenRepository.save(token);

        String enlace = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/restablecer-password")
                .queryParam("token", tokenPlano)
                .build()
                .toUriString();

        log.info("============================================================");
        log.info("ENLACE DE RECUPERACIÓN PARA ENTORNO ACADÉMICO LOCAL");
        log.info("Usuario: {}", usuario.getCorreo());
        log.info("Válido durante 15 minutos y para un solo uso:");
        log.info("{}", enlace);
        log.info("============================================================");
    }

    @Transactional(readOnly = true)
    public boolean tokenEsValido(String tokenPlano) {
        if (tokenPlano == null || tokenPlano.isBlank()) {
            return false;
        }

        return tokenRepository
                .findByTokenHash(calcularHash(tokenPlano))
                .map(token -> token.estaVigente(LocalDateTime.now()))
                .orElse(false);
    }

    @Transactional
    public void restablecerPassword(
            String tokenPlano,
            String nuevaClave,
            String confirmacion
    ) {
        validarNuevaClave(nuevaClave, confirmacion);

        if (tokenPlano == null || tokenPlano.isBlank()) {
            throw new IllegalArgumentException(
                    "El enlace de recuperación no es válido."
            );
        }

        PasswordResetToken token = tokenRepository
                .findByTokenHash(calcularHash(tokenPlano))
                .orElseThrow(() -> new IllegalArgumentException(
                        "El enlace de recuperación no es válido."
                ));

        if (!token.estaVigente(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "El enlace expiró o ya fue utilizado. Solicite uno nuevo."
            );
        }

        Usuario usuario = token.getUsuario();
        if (usuario == null || !usuario.isEstado()) {
            throw new IllegalArgumentException(
                    "La cuenta asociada no se encuentra disponible."
            );
        }

        usuario.setClave(passwordEncoder.encode(nuevaClave));
        usuarioRepository.save(usuario);

        token.setUtilizado(true);
        tokenRepository.save(token);
    }

    private void validarNuevaClave(String nuevaClave, String confirmacion) {
        if (nuevaClave == null || nuevaClave.length() < LONGITUD_MINIMA_CLAVE) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres."
            );
        }

        if (!nuevaClave.equals(confirmacion)) {
            throw new IllegalArgumentException(
                    "Las contraseñas ingresadas no coinciden."
            );
        }
    }

    private String generarTokenSeguro() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String calcularHash(String tokenPlano) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    tokenPlano.getBytes(StandardCharsets.UTF_8)
            );
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "No se pudo procesar el token de recuperación.",
                    e
            );
        }
    }
}
