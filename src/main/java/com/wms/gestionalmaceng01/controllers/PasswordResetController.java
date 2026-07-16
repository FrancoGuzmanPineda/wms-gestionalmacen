package com.wms.gestionalmaceng01.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wms.gestionalmaceng01.services.PasswordResetService;

@Controller
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(
            PasswordResetService passwordResetService
    ) {
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/recuperar-password")
    public String mostrarSolicitud() {
        return "recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String solicitarRecuperacion(
            @RequestParam String correo,
            RedirectAttributes redirectAttributes
    ) {
        passwordResetService.solicitarRecuperacion(correo);

        redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "Solicitud procesada. En este entorno académico local, "
                        + "revise la consola de Visual Studio Code para obtener el enlace."
        );

        return "redirect:/recuperar-password";
    }

    @GetMapping("/restablecer-password")
    public String mostrarRestablecimiento(
            @RequestParam(required = false) String token,
            Model model
    ) {
        boolean tokenValido = passwordResetService.tokenEsValido(token);
        model.addAttribute("token", token);
        model.addAttribute("tokenValido", tokenValido);

        if (!tokenValido) {
            model.addAttribute(
                    "mensajeError",
                    "El enlace no es válido, expiró o ya fue utilizado."
            );
        }

        return "restablecer-password";
    }

    @PostMapping("/restablecer-password")
    public String restablecerPassword(
            @RequestParam String token,
            @RequestParam String nuevaClave,
            @RequestParam String confirmarClave,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            passwordResetService.restablecerPassword(
                    token,
                    nuevaClave,
                    confirmarClave
            );

            redirectAttributes.addFlashAttribute(
                    "passwordResetSuccess",
                    "Contraseña actualizada correctamente. Ya puede iniciar sesión."
            );
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("token", token);
            model.addAttribute(
                    "tokenValido",
                    passwordResetService.tokenEsValido(token)
            );
            model.addAttribute("mensajeError", e.getMessage());
            return "restablecer-password";
        }
    }
}
