package com.wms.gestionalmaceng01;

import java.awt.Desktop;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Gestionalmaceng01Application {

    private static final Logger logger = LoggerFactory.getLogger(Gestionalmaceng01Application.class);

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.browser.enabled:true}")
    private boolean aperturaAutomaticaHabilitada;

    public static void main(String[] args) {
        SpringApplication.run(Gestionalmaceng01Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegador() {
        String loginUrl = normalizarBaseUrl(baseUrl) + "/login";
        logger.info("Servidor iniciado en: {}", loginUrl);

        // Durante las pruebas automáticas esta opción se desactiva para evitar
        // abrir ventanas o ejecutar comandos del sistema operativo.
        if (!aperturaAutomaticaHabilitada) {
            logger.info("Apertura automática del navegador deshabilitada para este entorno.");
            return;
        }

        try {
            URI uri = URI.create(loginUrl);

            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
                return;
            }

            abrirConComandoDelSistemaOperativo(loginUrl);
        } catch (Exception exception) {
            logger.warn(
                    "No se pudo abrir el navegador automáticamente. Abra manualmente: {}",
                    loginUrl,
                    exception);
        }
    }

    private String normalizarBaseUrl(String url) {
        if (url == null || url.isBlank()) {
            return "http://localhost:8080";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private void abrirConComandoDelSistemaOperativo(String url) throws Exception {
        String sistemaOperativo = System.getProperty("os.name", "").toLowerCase();

        if (sistemaOperativo.contains("win")) {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
        } else if (sistemaOperativo.contains("mac")) {
            new ProcessBuilder("open", url).start();
        } else {
            new ProcessBuilder("xdg-open", url).start();
        }
    }
}
