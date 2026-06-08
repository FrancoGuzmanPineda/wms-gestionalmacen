package com.wms.gestionalmaceng01.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

public class ForgotPasswordController {
    // Muestra la pantalla de recuperación
    @GetMapping("/recuperarclave")
    public String showForgotPasswordForm() {
        return "recuperarclave"; // Buscará recuperar-password.html en templates
    }

    // Procesa el envío del formulario
    @PostMapping("/recuperarclave")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        
        // Enviamos un mensaje de éxito al login
        redirectAttributes.addFlashAttribute("successMessage", "Si el correo existe, recibirás las instrucciones de recuperación.");
        return "redirect:/login"; 
    }
}
