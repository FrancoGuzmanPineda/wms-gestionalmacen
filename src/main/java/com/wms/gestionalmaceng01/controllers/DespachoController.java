package com.wms.gestionalmaceng01.controllers;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wms.gestionalmaceng01.models.Despacho;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.DespachoService;
import com.wms.gestionalmaceng01.services.ProductoService;
import com.wms.gestionalmaceng01.services.UbicacionService;

@Controller
@RequestMapping("/despacho")
public class DespachoController {

    private final ProductoService productoService;
    private final UbicacionService ubicacionService;
    private final DespachoService despachoService;
    private final UsuarioRepository usuarioRepository;

    public DespachoController(
            ProductoService productoService,
            UbicacionService ubicacionService,
            DespachoService despachoService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
        this.despachoService = despachoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String mostrarFormulario(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        agregarDatosUsuario(model, auth);
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("ubicaciones", ubicacionService.listarActivos());
        model.addAttribute("despacho", new Despacho());

        return "despacho/despacho";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute Despacho despacho,
            @RequestParam Integer idProducto,
            @RequestParam Integer idUbicacion,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        try {
            despachoService.registrarDespacho(
                    idProducto,
                    idUbicacion,
                    auth.getName(),
                    despacho
            );

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "El despacho y el movimiento de salida fueron registrados correctamente."
            );
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }

        return "redirect:/despacho";
    }

    private void agregarDatosUsuario(Model model, Authentication auth) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(auth.getName());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("rol", usuario.getRol());
            model.addAttribute("correo", usuario.getCorreo());
            return;
        }

        model.addAttribute("nombre", "Usuario");
        model.addAttribute("rol", "ROL");
    }
}
