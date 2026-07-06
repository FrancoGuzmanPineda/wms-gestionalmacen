package com.wms.gestionalmaceng01.controllers;

import com.wms.gestionalmaceng01.services.DashboardPdfService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.time.LocalDate;

@Controller
public class DashboardPdfController {

    private final DashboardPdfService dashboardPdfService;

    public DashboardPdfController(DashboardPdfService dashboardPdfService) {
        this.dashboardPdfService = dashboardPdfService;
    }

    // Descarga el PDF con la información actual del dashboard.
    @GetMapping("/dashboard/pdf")
    public void descargarPdfDashboard(
            Authentication authentication,
            HttpServletResponse response
    ) throws IOException {

        String usuarioLogueado = authentication != null
                ? authentication.getName()
                : "Usuario";

        byte[] pdf = dashboardPdfService.generarPdfDashboard(usuarioLogueado);

        String nombreArchivo = "dashboard-wms-" + LocalDate.now() + ".pdf";

        response.setContentType("application/pdf");
        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + nombreArchivo + "\""
        );
        response.setContentLength(pdf.length);

        response.getOutputStream().write(pdf);
        response.getOutputStream().flush();
    }
}
