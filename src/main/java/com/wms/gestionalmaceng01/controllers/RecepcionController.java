package com.wms.gestionalmaceng01.controllers;

import com.wms.gestionalmaceng01.services.ProductoService;
import com.wms.gestionalmaceng01.services.UbicacionService;
import com.wms.gestionalmaceng01.services.TipoService;
import com.wms.gestionalmaceng01.services.RecepcionService;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recepcion")
public class RecepcionController {

    private final ProductoService productoService;
    private final UbicacionService ubicacionService;
    private final TipoService tipoService;
    private final RecepcionService recepcionService;
    private final UsuarioRepository usuarioRepository;

    public RecepcionController(
            ProductoService productoService,
            UbicacionService ubicacionService,
            TipoService tipoService,
            RecepcionService recepcionService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.ubicacionService = ubicacionService;
        this.tipoService = tipoService;
        this.recepcionService = recepcionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String mostrarFormularioRecepcion(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("ubicaciones", ubicacionService.listarActivos());
        model.addAttribute("tipos", tipoService.listarActivos());
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("movimientos", recepcionService.listarMovimientosIngreso());

        return "recepcion/formulario";
    }

    @PostMapping("/registrar")
    public String registrarRecepcion(
            @RequestParam("idProducto") Integer idProducto,
            @RequestParam("idUbicacion") Integer idUbicacion,
            @RequestParam("idTipo") Integer idTipo,
            @RequestParam("idUsuario") Long idUsuario,
            @RequestParam("cantidad") Integer cantidad,
            @RequestParam(value = "observacion", required = false) String observacion
    ) {
        recepcionService.registrarRecepcion(
                idProducto,
                idUbicacion,
                idTipo,
                idUsuario,
                cantidad,
                observacion
        );

        return "redirect:/inventario";
    }
}