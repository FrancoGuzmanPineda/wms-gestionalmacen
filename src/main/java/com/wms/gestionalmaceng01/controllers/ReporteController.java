package com.wms.gestionalmaceng01.controllers;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.ReporteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/reportes")
public class ReporteController {

    private final ReporteService reporteService;
    private final UsuarioRepository usuarioRepository;

    public ReporteController(
            ReporteService reporteService,
            UsuarioRepository usuarioRepository
    ) {
        this.reporteService = reporteService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String index(Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);
        return "reportes/index";
    }

    @GetMapping("/entradas")
    public String entradas(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            Model model,
            Authentication auth
    ) {

        agregarDatosUsuario(model, auth);

        model.addAttribute(
                "movimientos",
                reporteService.obtenerEntradas(fechaInicio, fechaFin)
        );

        return "reportes/entradas";
    }

    @GetMapping("/salidas")
    public String salidas(
            @RequestParam(required = false) LocalDate fechaInicio,
            @RequestParam(required = false) LocalDate fechaFin,
            Model model,
            Authentication auth
    ) {

        agregarDatosUsuario(model, auth);

        model.addAttribute(
                "movimientos",
                reporteService.obtenerSalidas(fechaInicio, fechaFin)
        );

        return "reportes/salidas";
    }

    @GetMapping("/inventario")
    public String inventario(
            @RequestParam(required = false) String buscar,
            Model model,
            Authentication auth
    ) {

        agregarDatosUsuario(model, auth);

        model.addAttribute(
                "productos",
                reporteService.obtenerInventario(buscar)
        );

        return "reportes/inventario";
    }

    private void agregarDatosUsuario(Model model, Authentication auth) {

        if (auth != null) {

            Optional<Usuario> usuarioOpt =
                    usuarioRepository.findByCorreo(auth.getName());

            if (usuarioOpt.isPresent()) {

                Usuario usuario = usuarioOpt.get();

                model.addAttribute("nombre", usuario.getNombre());
                model.addAttribute("rol", usuario.getRol());
                model.addAttribute("correo", usuario.getCorreo());

                return;
            }
        }

        model.addAttribute("nombre", "Usuario");
        model.addAttribute("rol", "ROL");
    }
}