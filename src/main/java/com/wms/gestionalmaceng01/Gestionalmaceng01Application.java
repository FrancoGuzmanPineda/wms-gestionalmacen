package com.wms.gestionalmaceng01;

import java.awt.Desktop;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Gestionalmaceng01Application {

    private static final Logger logger = LoggerFactory.getLogger(Gestionalmaceng01Application.class);
    private static final String APPLICATION_URL = "http://localhost:8080/login";

    public static void main(String[] args) {
        SpringApplication.run(Gestionalmaceng01Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void abrirNavegador() {
        logger.info("Servidor iniciado en: {}", APPLICATION_URL);

        try {
            URI uri = URI.create(APPLICATION_URL);

            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
                return;
            }

            abrirConComandoDelSistemaOperativo(APPLICATION_URL);
        } catch (Exception exception) {
            logger.warn(
                    "No se pudo abrir el navegador automáticamente. Abra manualmente: {}",
                    APPLICATION_URL,
                    exception);
        }
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
