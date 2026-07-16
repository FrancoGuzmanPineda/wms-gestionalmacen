package com.wms.gestionalmaceng01.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wms.gestionalmaceng01.models.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByCorreoIgnoreCase(String correo);
    boolean existsByCorreo(String correo);
}
