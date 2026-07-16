package com.wms.gestionalmaceng01.controllers;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wms.gestionalmaceng01.dto.RecepcionForm;
import com.wms.gestionalmaceng01.models.Movimiento;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.InventarioUbicacionService;
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
    private final InventarioUbicacionService inventarioUbicacionService;
    private final UsuarioRepository usuarioRepository;

    public RecepcionController(
            ProductoService productoService,
            UbicacionService ubicacionService,
            RecepcionService recepcionService,
            InventarioUbicacionService inventarioUbicacionService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
        this.recepcionService = recepcionService;
        this.inventarioUbicacionService = inventarioUbicacionService;
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

        if (!model.containsAttribute("recepcionForm")) {
            model.addAttribute("recepcionForm", new RecepcionForm());
        }

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
                recepcionService.listarUltimosMovimientosIngreso()
        );

        return "recepcion/formulario";
    }

    @GetMapping("/api/productos/{idProducto}/ubicaciones/{idUbicacion}/stock")
    @ResponseBody
    public ResponseEntity<Integer> obtenerStockUbicacion(
            @PathVariable Integer idProducto,
            @PathVariable Integer idUbicacion
    ) {
        int stock = inventarioUbicacionService
                .obtenerStockPorProductoYUbicacion(
                        idProducto,
                        idUbicacion
                );

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(stock);
    }

    @PostMapping("/registrar")
    public String registrarRecepcion(
            @ModelAttribute("recepcionForm") RecepcionForm recepcionForm,
            BindingResult bindingResult,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {
        if (noEstaAutenticado(auth)) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            conservarFormularioConError(
                    recepcionForm,
                    "Revise los datos ingresados. Producto, ubicación y cantidad deben tener valores válidos.",
                    redirectAttributes
            );
            return "redirect:/recepcion";
        }

        try {
            Movimiento movimiento = recepcionService.registrarRecepcion(
                    recepcionForm.getIdProducto(),
                    recepcionForm.getIdUbicacion(),
                    auth.getName(),
                    recepcionForm.getCantidad(),
                    recepcionForm.getObservacion()
            );

            redirectAttributes.addFlashAttribute(
                    "mensajeExito",
                    construirMensajeExito(movimiento)
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            conservarFormularioConError(
                    recepcionForm,
                    e.getMessage(),
                    redirectAttributes
            );
        }

        return "redirect:/recepcion";
    }

    private void conservarFormularioConError(
            RecepcionForm recepcionForm,
            String mensaje,
            RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("recepcionForm", recepcionForm);
        redirectAttributes.addFlashAttribute("mensajeError", mensaje);
    }

    private String construirMensajeExito(Movimiento movimiento) {
        String unidad = movimiento.getProducto().getUnidadMedida();
        if (unidad == null || unidad.isBlank()) {
            unidad = "unidades";
        }

        return "Recepción N.° "
                + movimiento.getIdMovimiento()
                + " registrada: "
                + movimiento.getCantidad()
                + " "
                + unidad
                + " de "
                + movimiento.getProducto().getNombre()
                + " en la ubicación "
                + movimiento.getUbicacion().getCodigoEstante()
                + ".";
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
