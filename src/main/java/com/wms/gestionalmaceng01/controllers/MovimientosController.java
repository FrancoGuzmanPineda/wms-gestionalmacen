package com.wms.gestionalmaceng01.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.RecepcionService;

@Controller
@RequestMapping("/movimientos")
public class MovimientosController {

    private final UsuarioRepository usuarioRepository;
    private final RecepcionService recepcionService;

    public MovimientosController(
            UsuarioRepository usuarioRepository,
            RecepcionService recepcionService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.recepcionService = recepcionService;
    }

    @GetMapping
    public String listarMovimientos(Authentication auth, Model model) {
        if (auth != null) {
            usuarioRepository.findByCorreo(auth.getName()).ifPresent(usuario -> {
                model.addAttribute("nombre", usuario.getNombre());
                model.addAttribute("rol", usuario.getRol());
            });
        }

        model.addAttribute(
            "movimientos",
            recepcionService.listarMovimientosIngreso()
        );

        return "movimientos/mlistar";
    }
}
