package com.wms.gestionalmaceng01.controllers;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.ProductoService;
import com.wms.gestionalmaceng01.services.RecepcionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class InventarioController {

    private final ProductoService productoService;
    private final RecepcionService recepcionService;
    private final UsuarioRepository usuarioRepository;

    public InventarioController(
            ProductoService productoService,
            RecepcionService recepcionService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.recepcionService = recepcionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/inventario")
    public String mostrarInventario(Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("movimientos", recepcionService.listarMovimientosIngreso());

        return "inventario/listar";
    }

    private void agregarDatosUsuario(Model model, Authentication auth) {
        if (auth != null) {
            String correo = auth.getName();
            Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(correo);

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