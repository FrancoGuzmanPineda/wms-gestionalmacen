package com.wms.gestionalmaceng01;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Gestionalmaceng01Application {

	private static final Logger logger = LoggerFactory.getLogger(Gestionalmaceng01Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Gestionalmaceng01Application.class, args);
		logger.info("Servidor iniciado en: http://localhost:8080/login");
	}

}
