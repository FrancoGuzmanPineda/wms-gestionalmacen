package com.wms.gestionalmaceng01.controllers;

import com.wms.gestionalmaceng01.models.Producto;
import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.CategoriaService;
import com.wms.gestionalmaceng01.services.ProductoService;
import com.wms.gestionalmaceng01.services.TipoService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final TipoService tipoService;
    private final UsuarioRepository usuarioRepository;

    public ProductoController(
            ProductoService productoService,
            CategoriaService categoriaService,
            TipoService tipoService,
            UsuarioRepository usuarioRepository
    ) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.tipoService = tipoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listarProductos(Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        model.addAttribute("productos", productoService.listarTodos());
        return "productos/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.listarActivos());
        model.addAttribute("tipos", tipoService.listarActivos());

        return "productos/formulario";
    }

    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto) {
        productoService.guardar(producto);
        return "redirect:/productos";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable("id") Integer id, Model model, Authentication auth) {
        agregarDatosUsuario(model, auth);

        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarActivos());
        model.addAttribute("tipos", tipoService.listarActivos());

        return "productos/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        productoService.eliminarLogico(id);
        return "redirect:/productos";
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