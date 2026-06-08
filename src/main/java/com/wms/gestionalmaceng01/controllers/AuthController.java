package com.wms.gestionalmaceng01.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wms.gestionalmaceng01.models.Usuario;
import com.wms.gestionalmaceng01.repository.UsuarioRepository;

@Controller
public class AuthController {
    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("correo", auth.getName());
        model.addAttribute("rol", auth.getAuthorities().toString());
        return "dashboard";
    }

    @GetMapping("/api/verificar-usuario")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> verificarUsuario(@RequestParam String email) {
        Map<String, Object> respuesta = new HashMap<>();
        var userOpt = userRepository.findByCorreo(email);

        if (userOpt.isEmpty()) {
            respuesta.put("existe", false);
        } else {
            Usuario user = userOpt.get();
            respuesta.put("existe", true);
            respuesta.put("rol", user.getRol());
            respuesta.put("bloqueado", user.estaBloqueado());
            respuesta.put("segundosBloqueo", user.getSegundosBloqueo());
            respuesta.put("intentosRestantes", Math.max(0, 3 - user.getIntentosFallidos()));
        }
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/api/cambiar-password")
    @ResponseBody
    public ResponseEntity<Map<String, String>> cambiarPassword(@RequestBody Map<String, String> datos) {
        Map<String, String> respuesta = new HashMap<>();
        String email = datos.get("email");
        String actual = datos.get("actual");
        String nueva = datos.get("nueva");

        var userOpt = userRepository.findByCorreo(email);
        if (userOpt.isEmpty()) {
            respuesta.put("Error", "Usuario no encontrado");
            return ResponseEntity.badRequest().body(respuesta);
        }

        Usuario user = userOpt.get();
        if (!passwordEncoder.matches(actual, user.getClave())) {
            respuesta.put("error", "Contraseña actual incorrecta");
            return ResponseEntity.badRequest().body(respuesta);
        }

        user.setClave(passwordEncoder.encode(nueva));
        user.reiniciarIntentos();
        userRepository.save(user);

        respuesta.put("mensaje", "Contraseña actualizada");
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/api/crear-usuario")
    @ResponseBody
    public ResponseEntity<Map<String, String>> crearUsuario(@RequestBody Map<String, String> datos) {
        Map<String, String> respuesta = new HashMap<>();
        
        // El frontend envía "correo" o "email"? Asegúrate que coincida
        String correo = datos.get("correo");
        if (correo == null) {
            correo = datos.get("email");  // Por si el frontend envía "email"
        }

        if (userRepository.existsByCorreo(correo)) {
            respuesta.put("error", "El correo ya existe");
            return ResponseEntity.badRequest().body(respuesta);
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(datos.get("nombre"));
        nuevo.setCorreo(correo);
        nuevo.setClave(passwordEncoder.encode(datos.get("clave")));
        nuevo.setRol(datos.get("rol"));

        userRepository.save(nuevo);
        respuesta.put("mensaje", "Usuario creado exitosamente");
        return ResponseEntity.ok(respuesta);
    }
}