package com.wms.gestionalmaceng01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wms.gestionalmaceng01.models.Despacho;
import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.services.DespachoService;
import com.wms.gestionalmaceng01.services.ProductoService;

@Controller
@RequestMapping("/despacho")
public class DespachoController {

    private final ProductoService productoService;
    private final DespachoService despachoService;

    public DespachoController(
            ProductoService productoService,
            DespachoService despachoService) {

        this.productoService = productoService;
        this.despachoService = despachoService;
    }

 @GetMapping
public String mostrarFormulario(Model model) {

    model.addAttribute("productos",
            productoService.listarTodos());

    model.addAttribute("despacho",
            new Despacho());

    return "despacho/despacho";
}

    @PostMapping("/guardar")
public String guardar(
        @ModelAttribute Despacho despacho,
        @RequestParam Integer idProducto) {

    Producto producto =
            productoService.buscarPorId(idProducto)
                    .orElseThrow(() ->
                            new RuntimeException("Producto no encontrado"));

    productoService.disminuirStock(
            idProducto,
            despacho.getCantidad());

    despacho.setProducto(producto);

    despachoService.guardar(despacho);

    return "redirect:/despacho";
}
}