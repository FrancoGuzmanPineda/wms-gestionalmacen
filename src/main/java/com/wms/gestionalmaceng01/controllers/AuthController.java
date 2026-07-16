package com.wms.gestionalmaceng01.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;
import com.wms.gestionalmaceng01.services.DashboardService;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private static final String DASHBOARD_ADMIN = "dashboardadmin";
    private static final String ERROR_KEY = "error";

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DashboardService dashboardService;

    public AuthController(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            DashboardService dashboardService) {
        this.userRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {

        if (auth == null || auth.getName() == null) {
            return "redirect:/login";
        }

        String correo = auth.getName();
        model.addAttribute("correo", correo);

        Optional<Usuario> usuarioOpt = userRepository.findByCorreo(correo);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            String rol = usuario.getRol();

            model.addAttribute("nombre", usuario.getNombre());
            model.addAttribute("rol", rol);

            log.info("Usuario autenticado: {} - Rol: {}", correo, rol);

            if ("ADMIN".equals(rol) || "SUPERVISOR".equals(rol)) {
                // Carga datos reales de MySQL para el dashboard.
                cargarDatosDashboard(model);
                return DASHBOARD_ADMIN;
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/api/cambiar-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cambiarPassword(@RequestBody Map<String, String> datos) {
        Map<String, String> respuesta = new HashMap<>();

        String email = datos.get("email");
        String actual = datos.get("actual");
        String nueva = datos.get("nueva");

        Optional<Usuario> userOpt = userRepository.findByCorreo(email);

        if (userOpt.isEmpty()) {
            respuesta.put(ERROR_KEY, "Usuario no encontrado");
            return ResponseEntity.badRequest().body(respuesta);
        }

        Usuario user = userOpt.get();

        if (!passwordEncoder.matches(actual, user.getClave())) {
            respuesta.put(ERROR_KEY, "Contraseña actual incorrecta");
            return ResponseEntity.badRequest().body(respuesta);
        }

        user.setClave(passwordEncoder.encode(nueva));
        userRepository.save(user);

        respuesta.put("mensaje", "Contraseña actualizada correctamente");
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/api/crear-usuario")
    @ResponseBody
    public ResponseEntity<Map<String, String>> crearUsuario(@RequestBody Map<String, String> datos) {

        Map<String, String> respuesta = new HashMap<>();

        String nombre = datos.get("nombre");
        String correo = datos.get("correo");
        String clave = datos.get("clave");
        String rol = datos.get("rol");

        if (nombre == null || nombre.trim().isEmpty()) {
            respuesta.put(ERROR_KEY, "El campo nombre es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }

        if (correo == null || correo.trim().isEmpty()) {
            respuesta.put(ERROR_KEY, "El campo correo es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }

        if (clave == null || clave.trim().isEmpty()) {
            respuesta.put(ERROR_KEY, "El campo contraseña es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }

        if (rol == null || rol.trim().isEmpty()) {
            respuesta.put(ERROR_KEY, "El campo rol es obligatorio");
            return ResponseEntity.badRequest().body(respuesta);
        }

        if (userRepository.existsByCorreo(correo)) {
            respuesta.put(ERROR_KEY, "El correo ya existe");
            return ResponseEntity.badRequest().body(respuesta);
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setClave(passwordEncoder.encode(clave));
        nuevo.setRol(rol);
        nuevo.setEstado(true);
        userRepository.save(nuevo);

        respuesta.put("mensaje", "Usuario creado exitosamente");
        return ResponseEntity.ok(respuesta);
    }

    // Agrega al modelo los datos reales que se mostrarán en el dashboard.
    private void cargarDatosDashboard(Model model) {

        model.addAllAttributes(
                dashboardService.obtenerIndicadores());

        model.addAttribute(
                "ultimosMovimientos",
                dashboardService.obtenerUltimosMovimientos());

        model.addAttribute(
                "productosCriticos",
                dashboardService.obtenerProductosCriticos());

        model.addAllAttributes(
        dashboardService.obtenerDatosGraficos()
);        
    }
}