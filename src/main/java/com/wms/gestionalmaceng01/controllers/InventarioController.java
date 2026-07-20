package com.wms.gestionalmaceng01.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.InventarioUbicacionService;
import com.wms.gestionalmaceng01.services.ProductoService;

@Controller
public class InventarioController {
    private final ProductoService productoService;
    private final InventarioUbicacionService inventarioUbicacionService;
    private final UsuarioRepository usuarioRepository;

    public InventarioController(
            ProductoService productoService,
            InventarioUbicacionService inventarioUbicacionService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.inventarioUbicacionService = inventarioUbicacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/inventario")
    public String mostrarInventario(
            @RequestParam(value = "buscar", required = false)
            String buscar,
            Model model,
            Authentication auth
    ) {
        agregarDatosUsuario(model, auth);

        List<Producto> productos =
                productoService.buscarInventarioActivo(buscar);

        model.addAttribute("productos", productos);
        model.addAttribute(
                "stockPorProducto",
                inventarioUbicacionService.agruparPorProducto(productos)
        );
        model.addAttribute(
                "buscar",
                buscar == null ? "" : buscar.trim()
        );

        return "inventario/listar";
    }

    private void agregarDatosUsuario(
            Model model,
            Authentication auth
    ) {
        if (auth != null) {
            String correo = auth.getName();
            Optional<Usuario> usuarioOpt =
                    usuarioRepository.findByCorreo(correo);

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
