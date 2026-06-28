package com.wms.gestionalmaceng01.controllers;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listarUsuarios(Authentication auth, Model model) {
        // Datos del usuario autenticado
        if (auth != null) {
            String correo = auth.getName();
            usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
                model.addAttribute("nombre", usuario.getNombre());
                model.addAttribute("rol", usuario.getRol());
            });
        }

        // Obtener todos los usuarios de la base de datos
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/ulistar";
    }

        // ===== MOSTRAR FORMULARIO NUEVO =====
        @GetMapping("/uformulario")
        public String nuevoUsuario(Authentication auth, Model model) {
        if (auth != null) {
            String correo = auth.getName();
            usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
                model.addAttribute("nombre", usuario.getNombre());
                model.addAttribute("rol", usuario.getRol());
            });
        }

        Usuario nuevo = new Usuario();
        nuevo.setEstado(true);
        
        model.addAttribute("usuario", nuevo);
        return "usuarios/uformulario";
    }

    // ===== GUARDAR USUARIO =====
    @SuppressWarnings("squid:S4684")
    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        usuario.setEstado(true);
        usuario.reiniciarIntentos();
        
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable Long id, Authentication auth, Model model) {
        if (auth != null) {
        String correo = auth.getName();
        usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("rol", usuario.getRol());
        });
    }

    Usuario usuario = usuarioRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        model.addAttribute("usuario", usuario);
        return "usuarios/ueditar";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute Usuario usuarioActualizado) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        usuario.setNombre(usuarioActualizado.getNombre());
        usuario.setCorreo(usuarioActualizado.getCorreo());
        usuario.setRol(usuarioActualizado.getRol());
        usuario.setEstado(usuarioActualizado.isEstado());
    
    // Solo actualizar contraseña si se envió una nueva
        if (usuarioActualizado.getClave() != null && !usuarioActualizado.getClave().isEmpty()) {
            usuario.setClave(passwordEncoder.encode(usuarioActualizado.getClave()));
        }
    
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }
}