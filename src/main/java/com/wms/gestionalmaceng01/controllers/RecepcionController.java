package com.wms.gestionalmaceng01.controllers;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.ProductoService;
import com.wms.gestionalmaceng01.services.UbicacionService;
import com.wms.gestionalmaceng01.services.RecepcionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/recepcion")
public class RecepcionController {

    private final ProductoService productoService;
    private final UbicacionService ubicacionService;
    private final RecepcionService recepcionService;
    private final UsuarioRepository usuarioRepository;

    public RecepcionController(
            ProductoService productoService,
            UbicacionService ubicacionService,
            RecepcionService recepcionService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
        this.recepcionService = recepcionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String mostrarFormularioRecepcion(Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("ubicaciones", ubicacionService.listarActivos());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("movimientos", recepcionService.listarMovimientosIngreso());

        return "recepcion/formulario";
    }

    @PostMapping("/registrar")
    public String registrarRecepcion(
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("idUbicacion") Integer idUbicacion,
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam(value = "observacion", required = false) String observacion
    ) {
        recepcionService.registrarRecepcion(
                idProducto,
                idUbicacion,
                idUsuario,
                cantidad,
                observacion
        );

        return "redirect:/inventario";
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