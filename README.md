# Sistema de Gestión de Almacenes (WMS)

Aplicación web académica desarrollada con Java 21, Spring Boot, Thymeleaf,
Spring Security, JPA/Hibernate, MySQL y Gradle 8.7.

## Requisitos

- JDK 21 instalado.
- MySQL Server 8.0 o superior.
- MySQL Workbench.
- Visual Studio Code con soporte para Java y Spring Boot.
- Internet durante la primera ejecución para descargar Gradle 8.7 y las
  dependencias del proyecto.

Gradle no debe instalarse manualmente: el proyecto incluye Gradle Wrapper.

## Preparación de la base de datos

Ejecute en MySQL Workbench:

```text
database/WMS_EQUIPO_bdgestionalmaceng01.sql
```

El script elimina y vuelve a crear la base `bdgestionalmaceng01`. Incluye datos
de demostración, stock por ubicación, movimientos, despachos y tokens de
recuperación de contraseña. Realice una copia de seguridad antes de ejecutarlo
sobre una base con información propia.

La tabla `despachos` incluye `fecha_salida`, `fecha_llegada` y
`fecha_registro`, de acuerdo con la versión actualizada del módulo de despacho.

## Configuración de MySQL

La conexión se encuentra en:

```text
src/main/resources/application.properties
```

La versión entregada utiliza:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bdgestionalmaceng01?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
spring.datasource.username=root
spring.datasource.password=040319
```

Antes de ejecutar el sistema, verifique que MySQL esté iniciado, que la base
`bdgestionalmaceng01` exista y que las credenciales coincidan con la instalación
local. Si una computadora utiliza otra contraseña, deberá modificar únicamente
`spring.datasource.password` en este archivo.

## Credenciales iniciales del sistema

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@wms.com` | `123456` |
| Supervisor | `supervisor@wms.com` | `123456` |

## Gradle y clase principal

El archivo principal de construcción es:

```text
build.gradle
```

La clase de inicio está declarada explícitamente:

```groovy
springBoot {
    mainClass = 'com.wms.gestionalmaceng01.Gestionalmaceng01Application'
}
```

La versión oficial está fijada en Gradle 8.7 mediante:

```text
gradle/wrapper/gradle-wrapper.properties
```

## Verificación

En PowerShell, desde la carpeta raíz:

```powershell
java -version
.\gradlew.bat --version
```

El proyecto requiere JDK 21. Después ejecute:

```powershell
.\gradlew.bat clean build
```

## Ejecución para la sustentación

```powershell
.\gradlew.bat bootRun
```

En Linux o macOS:

```bash
./gradlew bootRun
```

Cuando Spring Boot termina de iniciar, se abre automáticamente:

```text
http://localhost:8080/login
```

## Funciones principales conservadas

- Autenticación y autorización para administrador y supervisor.
- Inventario y stock distribuido por ubicación.
- Recepción con validaciones, resumen previo y últimas 10 recepciones.
- Despacho actualizado con guía, fechas, destino, conductor y vehículo.
- Historial de movimientos.
- Dashboard y reporte PDF.
- Recuperación local de contraseña mediante token temporal.

## Recuperación de contraseña

La opción **¿Olvidó su contraseña?** genera un token temporal y de un solo uso.
Como el proyecto local no tiene servidor de correo, el enlace se muestra en la
consola. La URL base se configura mediante `app.base-url` en `application.properties`.

## Archivos de Gradle que deben subirse a GitHub

```text
build.gradle
settings.gradle
gradle.properties
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.properties
gradle/wrapper/gradle-wrapper.jar
```

No deben subirse `.gradle/`, `build/` ni `target/`.
