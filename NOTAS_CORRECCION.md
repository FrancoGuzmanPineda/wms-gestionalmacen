
1. `pom.xml`
   - Se restauró Spring Boot `3.5.14`.
   - Se restauró `java.version` a `17`, porque la versión con errores estaba en Java 25 y puede fallar en Visual Studio/VS Code si el JDK instalado no coincide.

2. Login / Spring Security
   - Se restauró `loginProcessingUrl("/perform_login")`.
   - Se cambió el formulario de `login.html` para enviar a `/perform_login`.
   - Se eliminó el controlador manual `POST /login`, porque la autenticación debe manejarla Spring Security.
   - Se restauró la redirección por rol: `ADMIN`, `EMPLOYEE`, `USER`.

3. MySQL
   - Se restauró el nombre de base de datos usado por la versión funcional: `bdgestionalmacen`.
   - Se restauró la contraseña del `application.properties` usada por la versión funcional. Si tu MySQL local tiene otra contraseña, cámbiala antes de ejecutar.

4. Usuarios
   - Se conservó el nuevo módulo `UsuarioController` y la vista `uformulario.html`.
   - Se agregó soporte básico para crear, editar y eliminar usuarios.
   - Se inicializa el campo `estado` como activo por defecto.
