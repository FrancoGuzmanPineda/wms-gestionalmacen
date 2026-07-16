# Sistema de Gestión de Almacenes (WMS)

Aplicación web académica desarrollada con Java 21, Spring Boot, Thymeleaf,
Spring Security, JPA/Hibernate y MySQL.

## Requisitos

- Java 21.
- MySQL Server 8.0 o superior.
- MySQL Workbench.
- Visual Studio Code con soporte para Java y Spring Boot.

## Instalación de la base de datos

Ejecute en MySQL Workbench el archivo:

```text
database/WMS_EQUIPO_bdgestionalmaceng01.sql
```

El script elimina y vuelve a crear la base `bdgestionalmaceng01`. Incluye:

- 20 productos de gaseosas y bebidas.
- 10 ubicaciones de almacén.
- Stock distribuido por producto y ubicación.
- Movimientos históricos de ingreso y salida.
- Despachos de demostración.
- Tabla de tokens para recuperación de contraseña.

Antes de ejecutarlo sobre una base con información propia, realice una copia de
seguridad.

## Credenciales iniciales

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@wms.com` | `123456` |
| Supervisor | `supervisor@wms.com` | `123456` |

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

## Stock por ubicación

La tabla `inventario_ubicaciones` controla la cantidad disponible de cada
producto en cada ubicación física.

1. Una recepción aumenta el stock de la ubicación elegida.
2. Un despacho consulta automáticamente las ubicaciones del producto.
3. Solo se muestran ubicaciones activas con stock mayor a cero.
4. La salida valida y descuenta el stock de la ubicación seleccionada.
5. `productos.stock_actual` se recalcula como la suma de todas sus ubicaciones.

## Recuperación de contraseña

La opción **¿Olvidó su contraseña?** genera un token seguro, temporal y de un
solo uso.

Como el proyecto se ejecuta localmente y no tiene servidor de correo, el enlace
de recuperación se muestra en la consola de Visual Studio Code. El enlace dura
15 minutos. La contraseña nueva se almacena cifrada con BCrypt.

La URL base utilizada para construir el enlace está configurada en:

```properties
app.base-url=http://localhost:8080
```

## Estructura principal

```text
database/
└─ WMS_EQUIPO_bdgestionalmaceng01.sql

src/main/java/com/wms/gestionalmaceng01/
├─ config/
├─ controllers/
├─ dto/
├─ models/
├─ repository/
├─ services/
└─ Gestionalmaceng01Application.java
```

## Control de versiones

El repositorio conserva su configuración de Git y la conexión con GitHub. Los
archivos generados por Maven (`target/`) y la configuración local de los IDE se
encuentran excluidos mediante `.gitignore`.
# Sistema de Gestión de Almacenes (WMS)

Aplicación web académica desarrollada con Java 21, Spring Boot, Thymeleaf,
Spring Security, JPA/Hibernate y MySQL.

## Requisitos

- Java 21.
- MySQL Server 8.0 o superior.
- MySQL Workbench.
- Visual Studio Code con soporte para Java y Spring Boot.

## Instalación de la base de datos

Ejecute en MySQL Workbench el archivo:

```text
database/WMS_EQUIPO_bdgestionalmaceng01.sql
```

El script elimina y vuelve a crear la base `bdgestionalmaceng01`. Incluye:

- 20 productos de gaseosas y bebidas.
- 10 ubicaciones de almacén.
- Stock distribuido por producto y ubicación.
- Movimientos históricos de ingreso y salida.
- Despachos de demostración.
- Tabla de tokens para recuperación de contraseña.

Antes de ejecutarlo sobre una base con información propia, realice una copia de
seguridad.

## Credenciales iniciales

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@wms.com` | `123456` |
| Supervisor | `supervisor@wms.com` | `123456` |

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

## Stock por ubicación

La tabla `inventario_ubicaciones` controla la cantidad disponible de cada
producto en cada ubicación física.

1. Una recepción aumenta el stock de la ubicación elegida.
2. Un despacho consulta automáticamente las ubicaciones del producto.
3. Solo se muestran ubicaciones activas con stock mayor a cero.
4. La salida valida y descuenta el stock de la ubicación seleccionada.
5. `productos.stock_actual` se recalcula como la suma de todas sus ubicaciones.

## Recuperación de contraseña

La opción **¿Olvidó su contraseña?** genera un token seguro, temporal y de un
solo uso.

Como el proyecto se ejecuta localmente y no tiene servidor de correo, el enlace
de recuperación se muestra en la consola de Visual Studio Code. El enlace dura
15 minutos. La contraseña nueva se almacena cifrada con BCrypt.

La URL base utilizada para construir el enlace está configurada en:

```properties
app.base-url=http://localhost:8080
```

## Estructura principal

```text
database/
└─ WMS_EQUIPO_bdgestionalmaceng01.sql

src/main/java/com/wms/gestionalmaceng01/
├─ config/
├─ controllers/
├─ dto/
├─ models/
├─ repository/
├─ services/
└─ Gestionalmaceng01Application.java
```

## Control de versiones

El repositorio conserva su configuración de Git y la conexión con GitHub. Los
archivos generados por Maven (`target/`) y la configuración local de los IDE se
encuentran excluidos mediante `.gitignore`.
