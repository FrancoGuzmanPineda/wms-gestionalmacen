package com.wms.gestionalmaceng01.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wms.gestionalmaceng01.repository.UsuarioRepository;

@Controller
@RequestMapping("/movimientos")
public class MovmientosController {

    private final UsuarioRepository usuarioRepository;

    public MovmientosController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listarMovimientos(Authentication auth, Model model) {
        if (auth != null) {
            String correo = auth.getName();
            usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
                model.addAttribute("nombre", usuario.getNombre());
                model.addAttribute("rol", usuario.getRol());
            });
        }

        return "movimientos/mlistar";
    }
}
