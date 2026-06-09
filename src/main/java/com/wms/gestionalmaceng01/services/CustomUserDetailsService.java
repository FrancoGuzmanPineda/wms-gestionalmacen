package com.wms.gestionalmaceng01.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el correo: " + email));

        // Determinar de manera segura si el usuario se encuentra bloqueado temporalmente
        boolean cuentaBloqueada = usuario.estaBloqueado();

        // Estructura limpia y nativa recomendada por Spring Security y SonarQube
        return User.withUsername(usuario.getCorreo())
            .password(usuario.getClave())
            .roles(usuario.getRol()) // Agrega el prefijo ROLE_ automáticamente para la sesión
            .accountLocked(cuentaBloqueada) // 🚨 Solución nativa: Spring Boot manejará el bloqueo de forma correcta
            .disabled(!usuario.isActivo()) // Deshabilita la sesión si el estado en BD no es activo
            .build();
    }
}