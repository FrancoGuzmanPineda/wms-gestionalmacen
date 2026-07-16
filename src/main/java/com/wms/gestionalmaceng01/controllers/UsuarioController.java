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
        cargarUsuarioSesion(auth, model);

        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/ulistar";
    }

    @GetMapping({"/nuevo", "/uformulario"})
    public String nuevoUsuario(Authentication auth, Model model) {
        cargarUsuarioSesion(auth, model);

        // ✅ Verificar que solo ADMIN pueda acceder
        if (auth == null || !auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/usuarios";
    }

        Usuario nuevo = new Usuario();
        nuevo.setEstado(true);
        model.addAttribute("usuario", nuevo);
        model.addAttribute("modoEdicion", false);
        return "usuarios/uformulario";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(@PathVariable("id") Long id, Authentication auth, Model model) {
        cargarUsuarioSesion(auth, model);

        Usuario usuario = usuarioRepository.findById(id).orElse(new Usuario());
        model.addAttribute("usuario", usuario);
        model.addAttribute("modoEdicion", true);
        return "usuarios/uformulario";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        if (usuario.getIdusuario() != null) {
            usuarioRepository.findById(usuario.getIdusuario()).ifPresent(usuarioExistente -> {
                usuarioExistente.setNombre(usuario.getNombre());
                usuarioExistente.setCorreo(usuario.getCorreo());
                usuarioExistente.setRol(usuario.getRol());
                usuarioExistente.setEstado(usuario.isEstado());

                if (usuario.getClave() != null && !usuario.getClave().isBlank()) {
                    usuarioExistente.setClave(passwordEncoder.encode(usuario.getClave()));
                }

                usuarioRepository.save(usuarioExistente);
            });
            return "redirect:/usuarios";
        }

        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        usuarioRepository.save(usuario);
        return "redirect:/usuarios";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id) {
        usuarioRepository.deleteById(id);
        return "redirect:/usuarios";
    }

    private void cargarUsuarioSesion(Authentication auth, Model model) {
        if (auth == null) {
            return;
        }

        String correo = auth.getName();
        usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("rol", usuario.getRol());
        });
    }
}
