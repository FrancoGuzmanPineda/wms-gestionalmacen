package com.wms.gestionalmaceng01.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wms.gestionalmaceng01.dto.UbicacionStockResponse;
import com.wms.gestionalmaceng01.models.Despacho;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.DespachoService;
import com.wms.gestionalmaceng01.services.InventarioUbicacionService;
import com.wms.gestionalmaceng01.services.ProductoService;

@Controller
@RequestMapping("/despacho")
public class DespachoController {

    private final ProductoService productoService;
    private final DespachoService despachoService;
    private final InventarioUbicacionService inventarioUbicacionService;
    private final UsuarioRepository usuarioRepository;

    public DespachoController(
            ProductoService productoService,
            DespachoService despachoService,
            InventarioUbicacionService inventarioUbicacionService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.despachoService = despachoService;
        this.inventarioUbicacionService = inventarioUbicacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String mostrarFormulario(Model model, Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }

        agregarDatosUsuario(model, auth);

        Usuario usuario = despachoService.obtenerUsuario(auth.getName());

        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("despacho", new Despacho());

        model.addAttribute("historial",
                despachoService.obtenerDespachos());

        model.addAttribute("esAdmin",
                despachoService.esAdministrador(usuario));

        model.addAttribute("empresas", Arrays.asList(
                "Tipo Negocio Autorizado - Lima Comas",
                "Tipo Negocio Autorizado - Lima Ate",
                "Tipo Negocio Autorizado - Lima Surco",
                "Tipo Negocio Autorizado - Callao",
                "Tipo Negocio Autorizado - Independencia"
        ));

        model.addAttribute("conductores", Arrays.asList(
                "Jose Colocho",
                "Carlos Ramos",
                "Luis Medina"
        ));

        model.addAttribute("placas", Arrays.asList(
                "ABC-123",
                "BFG-458",
                "CDE-951"
        ));

        return "despacho/despacho";
    }

    @GetMapping("/api/productos/{idProducto}/ubicaciones")
    @ResponseBody
    public ResponseEntity<List<UbicacionStockResponse>>
            listarUbicacionesConStock(
                    @PathVariable Integer idProducto
            ) {
        List<UbicacionStockResponse> respuesta = new ArrayList<>();

        inventarioUbicacionService
                .listarDisponiblesPorProducto(idProducto)
                .forEach(stock -> respuesta.add(
                        new UbicacionStockResponse(
                                stock.getUbicacion().getIdUbicacion(),
                                stock.getUbicacion().getCodigoEstante(),
                                stock.getUbicacion().getPasillo(),
                                stock.getUbicacion().getTipoUbicacion(),
                                stock.getCantidad()
                        )
                ));

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(respuesta);
    }

    @GetMapping("/nueva-guia")
    @ResponseBody
    public String nuevaGuia(){

        return despachoService.nuevaGuia();

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
                    "El despacho fue registrado y el stock de la ubicación "
                            + "fue actualizado correctamente."
            );
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "mensajeError",
                    e.getMessage()
            );
        }

        return "redirect:/despacho";
    }

    @PostMapping("/editar/{id}")
    public String editar(

            @PathVariable Integer id,
            @ModelAttribute Despacho despacho,
            RedirectAttributes redirect

    ){

        despachoService.actualizar(id, despacho);

        redirect.addFlashAttribute(
                "mensajeExito",
                "Despacho actualizado correctamente."
        );

        return "redirect:/despacho";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(

            @PathVariable Integer id,
            Authentication auth,
            RedirectAttributes redirect

    ){

        Usuario usuario =
                despachoService.obtenerUsuario(auth.getName());

        if(!despachoService.esAdministrador(usuario)){

            redirect.addFlashAttribute(
                    "mensajeError",
                    "No tiene permisos."
            );

            return "redirect:/despacho";
        }

        despachoService.eliminar(id);

        redirect.addFlashAttribute(
                "mensajeExito",
                "Despacho eliminado."
        );

        return "redirect:/despacho";

    }

    @GetMapping("/buscar/{id}")
    @ResponseBody
    public Despacho buscar(

            @PathVariable Integer id

    ){

        return despachoService.buscarPorId(id);

    }

    private void agregarDatosUsuario(
            Model model,
            Authentication auth
    ) {
        Optional<Usuario> usuarioOpt =
                usuarioRepository.findByCorreo(auth.getName());

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