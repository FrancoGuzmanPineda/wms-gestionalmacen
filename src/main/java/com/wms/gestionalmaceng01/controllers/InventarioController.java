package com.wms.gestionalmaceng01.controllers;

import com.wms.gestionalmaceng01.services.ProductoService;
import com.wms.gestionalmaceng01.services.RecepcionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InventarioController {

    private final ProductoService productoService;
    private final RecepcionService recepcionService;

    public InventarioController(ProductoService productoService, RecepcionService recepcionService) {
        this.productoService = productoService;
        this.recepcionService = recepcionService;
    }

    @GetMapping("/inventario")
    public String mostrarInventario(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        model.addAttribute("movimientos", recepcionService.listarMovimientosIngreso());
        return "inventario/listar";
    }
}