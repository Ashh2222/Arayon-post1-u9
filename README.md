# Post-contenido — Unidad 9: Seguridad en Aplicaciones Web

Laboratorio de Programación Web (Séptimo Semestre). Proyecto Maven Spring Boot
`seguridad-web/` (paquete `com.universidad.seguridad`). **Estado: Parte 1 completa;
la Parte 2 (JWT) se agrega en los siguientes commits.**

## Parte 1: Login con sesión, BCrypt, roles y mitigación activa

`SecurityConfig` define `webFilterChain` (`@Order(2)`) con form-login,
`BCryptPasswordEncoder(12)` y autorización por rol. CSRF queda activo porque
esta cadena depende de cookie de sesión.

### Login y roles
![Login](capturas/login.png)
Acceso a `/dashboard` sin sesión redirige a `/login`.

![Hash BCrypt en MySQL](capturas/bcrypt-mysql.png)
Las contraseñas se guardan como hash (`$2a$12$...`), nunca en texto claro.

![Admin 403](capturas/admin-403.png)
Un USER que entra a `/admin` recibe 403.

![Panel admin](capturas/admin-panel.png)
El ADMIN ve la lista de usuarios.

### XSS: `th:text` vs `th:utext`
![Dashboard seguro](capturas/dashboard-seguro.png)
Con `th:text`, el nombre `<script>alert("XSS")</script>` se muestra como texto escapado.

![XSS ejecutado con th:utext](capturas/xss-utext.png)
Prueba **temporal** con `th:utext`: el script se ejecuta. Se revirtió con `git restore`;
el repositorio solo contiene la versión segura.

### SQL Injection: `@Query` parametrizado
![Búsqueda normal](capturas/busqueda-normal.png)
![Inyección mitigada](capturas/sql-injection-mitigado.png)
Buscar `' OR '1'='1` devuelve 0 resultados: se trata como texto literal.

![Parámetro enlazado](capturas/sql-bind-consola.png)
Hibernate envía `like replace(?,...)` con el valor como parámetro enlazado.
Con una consulta concatenada, la condición siempre verdadera habría devuelto
todas las tareas de todos los usuarios.

### CSRF
![CSRF 403](capturas/csrf-403.png)
POST a `/logout` sin token: 403.

![Logout correcto](capturas/logout-ok.png)
Con el botón del dashboard (token incluido por Thymeleaf): redirige a `/login?logout`.

## Defectos del enunciado encontrados y cómo se resolvieron
1. **Boot 4.1.2 generado por start.spring.io** (Spring Security 7) incompatible con el código del enunciado: se fijó Boot 3.3.5 y se ajustaron los starters.
2. El dashboard mostraba el **email**, que no puede contener el payload XSS: ahora muestra el **nombre**.
3. `#usuarioId == authentication.principal.id` falla (el principal no tiene `id`): se identifica al usuario por **email**.
4. No existía el buscador de tareas que pide el Paso 8: se creó `TareaController` y la vista.
5. `buscarPorTitulo` devuelve tareas de todos los usuarios: se agregó `buscarPorTituloDe` filtrado por propietario.
6. Faltaba el bean `AuthenticationManager` (lo necesita la Parte 2): se agregó.
7. Faltaba `/error` en `permitAll`.
8. Mass assignment: `registrar` fija rol y `activo` en el servidor; `@InitBinder` limita los campos de Tarea.
9. `@NotBlank` en contraseña y `@Size` en tareas (el enunciado no los traía).
10. Secretos externalizados: la contraseña de BD va por variable de entorno `DB_PASSWORD`.

## Casos límite probados
| Caso | Resultado |
|---|---|
| Correo ya registrado | Error en el campo correo ![](capturas/registro-correo-existente.png) |
| Correo inválido | "Debe ser un correo válido" ![](capturas/registro-correo-invalido.png) |
| Nombre vacío | "El nombre es obligatorio" ![](capturas/registro-nombre-obligatorio.png) |
| Credenciales incorrectas | Mensaje de error en login ![](capturas/login-credenciales-incorrectas.png) |
| Buscar `%` | Devuelve solo las tareas propias (comodín de LIKE, no es inyección) ![](capturas/busqueda-comodin.png) |
| POST a `/tareas` sin token CSRF | 403 ![](capturas/csrf-post-tareas-403.png) |
| Usuario con `activo=0` | Login rechazado |
| Tabla `tareas` de la Unidad 8 en la misma BD | INSERT fallaba (error 1364, columna `codigo`); se eliminó la tabla y Hibernate la recreó. Lección: una BD por laboratorio |

## Cómo compilar y ejecutar
1. Clonar el repositorio.
2. Crear la base `seguridad_db` en MySQL y el usuario `appuser`.
3. Definir `DB_PASSWORD` como variable de entorno.
4. Insertar un usuario `ROLE_ADMIN` con hash BCrypt (rol y hash generados con `BCryptPasswordEncoder(12)`).
5. `cd seguridad-web` y `./mvnw spring-boot:run`.
6. Vistas web: http://localhost:8080/login