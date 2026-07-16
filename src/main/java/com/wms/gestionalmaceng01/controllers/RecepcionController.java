package com.wms.gestionalmaceng01.controllers;

import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.ProductoService;
import com.wms.gestionalmaceng01.services.RecepcionService;
import com.wms.gestionalmaceng01.services.UbicacionService;

@Controller
@RequestMapping("/recepcion")
public class RecepcionController {

    private static final Set<String> ROLES_AUTORIZADOS =
            Set.of("ADMIN", "SUPERVISOR");

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
    public String mostrarFormularioRecepcion(
            Model model,
            Authentication auth
    ) {
        if (noEstaAutenticado(auth)) {
            return "redirect:/login";
        }

        Optional<Usuario> usuarioOpt =
                usuarioRepository.findByCorreo(auth.getName());

        if (usuarioOpt.isEmpty() || !usuarioOpt.get().isEstado()) {
            return "redirect:/login?error=true";
        }

        Usuario usuario = usuarioOpt.get();

        if (!tieneRolAutorizado(usuario)) {
            return "redirect:/dashboard?accesoDenegado=true";
        }

        model.addAttribute("nombre", usuario.getNombre());
        model.addAttribute("rol", usuario.getRol());
        model.addAttribute("correo", usuario.getCorreo());

        model.addAttribute(
                "productos",
                productoService.listarActivos()
        );

        model.addAttribute(
                "ubicaciones",
                ubicacionService.listarActivos()
        );

        model.addAttribute(
                "movimientos",
                recepcionService.listarMovimientosIngreso()
        );

        return "recepcion/formulario";
    }

    @PostMapping("/registrar")
    public String registrarRecepcion(
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("idUbicacion") Integer idUbicacion,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam(value = "observacion", required = false)
            String observacion,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        if (noEstaAutenticado(auth)) {
            return "redirect:/login";
        }

        try {
            recepcionService.registrarRecepcion(
                    idProducto,
                    idUbicacion,
                    auth.getName(),
                    cantidad,
                    observacion
            );

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    "La recepción fue registrada y el stock de la ubicación fue actualizado correctamente."
            );

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage()
            );
        }

        return "redirect:/recepcion";
    }

    private boolean tieneRolAutorizado(Usuario usuario) {
        String rol = usuario.getRol() == null
                ? ""
                : usuario.getRol().trim().toUpperCase();

        return ROLES_AUTORIZADOS.contains(rol);
    }

    private boolean noEstaAutenticado(Authentication auth) {
        return auth == null
                || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getName());
    }
}