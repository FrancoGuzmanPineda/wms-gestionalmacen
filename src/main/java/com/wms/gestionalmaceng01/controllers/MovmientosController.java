package com.wms.gestionalmaceng01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movimientos")
public class MovmientosController {

    @GetMapping
    public String listarMovimientos() {
        return "movimientos/mlistar";
    }
    
}
