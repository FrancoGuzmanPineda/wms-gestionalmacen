package com.wms.gestionalmaceng01.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wms.gestionalmaceng01.models.PasswordResetToken;
import com.wms.gestionalmaceng01.models.Usuario;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteAllByUsuario(Usuario usuario);
}
