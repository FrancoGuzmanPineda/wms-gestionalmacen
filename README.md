# Sistema de Gestión de Almacenes (WMS)

Aplicación web académica desarrollada con Java 21, Spring Boot, Thymeleaf,
Spring Security, JPA/Hibernate y MySQL.

## Requisitos

- Java 21.
- MySQL Server y MySQL Workbench.
- Visual Studio Code con soporte para Java y Spring Boot.
- Base de datos `bdgestionalmaceng01` importada desde el archivo SQL del proyecto.

## Configuración de MySQL

La conexión se configura en:

```text
src/main/resources/application.properties
```

Verifique que el usuario y la contraseña coincidan con su instalación local de
MySQL. La aplicación utiliza por defecto:

```text
jdbc:mysql://localhost:3306/bdgestionalmaceng01
```

## Ejecución

En Windows, desde la carpeta principal del proyecto:

```powershell
.\mvnw.cmd clean spring-boot:run
```

Cuando Spring Boot termina de iniciar, el programa abre automáticamente:

```text
http://localhost:8080/login
```

## Estructura principal

```text
src/
├─ main/
│  ├─ java/com/wms/gestionalmaceng01/
│  │  ├─ config/
│  │  ├─ controllers/
│  │  ├─ models/
│  │  ├─ repository/
│  │  ├─ services/
│  │  └─ Gestionalmaceng01Application.java
│  └─ resources/
│     ├─ application.properties
│     ├─ static/
│     │  ├─ css/
│     │  └─ js/
│     └─ templates/
│        ├─ dashboardadmin.html
│        ├─ despacho/
│        ├─ fragments/
│        ├─ inventario/
│        ├─ movimientos/
│        ├─ productos/
│        ├─ recepcion/
│        └─ usuarios/
└─ test/
```

## Control de versiones

El repositorio conserva su configuración de Git y la conexión con GitHub. Los
archivos generados por Maven (`target/`) y la configuración local de los IDE se
encuentran excluidos mediante `.gitignore`.
