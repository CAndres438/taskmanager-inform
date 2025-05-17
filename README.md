# taskmanager-inform
# 🧠 Task Manager App - Backend (Spring Boot)

Bienvenido a la aplicación de gestión de tareas construida con **Spring Boot**, **JWT**, **WebSocket/STOMP** y **PostgreSQL**. Este backend cubre autenticación, control de acceso por roles, notificaciones en tiempo real, búsqueda avanzada y más.

---

### 🛠️ Requisitos

Antes de ejecutar el proyecto, asegúrate de tener instalado lo siguiente:

- Java 21 (JDK)
- Maven 3.8+
- PostgreSQL 15
- Navegador moderno para pruebas WebSocket (Chrome, Firefox)
- Opcional: Python 3 para servir el HTML

> 💡 Verifica que Java esté correctamente instalado con:
>
> ```bash
> java -version
> ```

Y que Maven esté disponible con:

```bash
mvn -v

```
---
## ⚙️ Instalación local

### 1. Clonar el repositorio
```bash
git clone https://github.com/CAndres438/taskmanager-inform.git
cd taskmanager-inform
```

### 2. Configurar la base de datos
Crea una base de datos PostgreSQL llamada `taskmanager`. 
Luego configura las credenciales en `src/main/resources/application-dev.properties`:

```properties
spring.datasource.url=jdbc:postgresql://{tu_ruta_local}/taskmanager # ejemplo:localhost:5432)
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

Habilita el perfil de desarrollo em `src/main/resources/application.properties`:

### 3. Levantar la aplicación


## 💡 Sugerencia: abrir con IntelliJ IDEA

Si usas IntelliJ IDEA, puedes abrir y ejecutar el proyecto fácilmente:

1. Abre IntelliJ.
2. Ve a **File > Open** y selecciona la carpeta raíz del proyecto.
3. IntelliJ reconocerá el proyecto como Maven automáticamente.
4. Si no lo hace, clic derecho en `pom.xml` → **Add as Maven Project**.
5. Espera a que descargue las dependencias.
6. Ejecuta desde el botón verde o con `mvn spring-boot:run`.

> También puedes usar cualquier otro editor compatible con Maven y Java 21.

```bash
mvn spring-boot:run
```
La app estará corriendo por defecto en `http://localhost:8080`

puedes cambiar con server.port si el puerto está ocupado

---

## 🔐 Rutas de Autenticación

### POST /auth/register
```json
{
  "name": "Andrés Ortiz",
  "email": "andresprueba@gmail.com",
  "password": "123456."
}
```
⚠️ **Importante:** Todos los usuarios nuevos registrados son tipo ROLE_USER
        por tanto solo tienen acceso a la visualización de tareas

### POST /auth/login
```json
{
  "email": "andresprueba@gmail.com",
  "password": "123456."
}
```

⚠️ **Importante:** solo un usuario con rol **ADMIN** puede crear, editar y elimnar tareas.  
En este proyecto, por defecto, debes estar autenticado como:

- **Email:** `admin@gmail.com`
- **Contraseña:** admin123


👉 Devuelve un token JWT que debe enviarse como header `Authorization: Bearer <token>`

## 👥 Usuarios predefinidos

| Rol   | Email            | Contraseña |
|--------|------------------|------------|
| ADMIN | admin@gmail.com  | admin123   |
| USER  | user@gmail.com   | user123    |

---

## 📦 CRUD de Tareas

| Método | Ruta           | Rol requerido |
|--------|----------------|----------------|
| GET    | api/tasks      | ADMIN, USER    |
| GET    | api/tasks/my   | USER           |
| POST   | api/tasks      | ADMIN          |
| PUT    | api/tasks/{id} | ADMIN          |
| DELETE | api/tasks/{id} | ADMIN          |

### Filtros, paginación y ordenamiento:
```http
GET api/tasks?title=plan&status=PENDING&page=0&size=5&sort=createdAt,desc
```

---

## 🔔 Notificaciones en Tiempo Real (WebSocket + STOMP)

Este proyecto usa Spring WebSocket para enviar notificaciones en tiempo real

ws://localhost:8080/ws

#### 🔧 Setup

- STOMP endpoint: `ws://<your-server>/ws`
- Suscripción: `/topic/tasks`
- Cuando una tarea es creada, el servidor envía un mensaje como

```json
{
  "id": 1,
  "title": "New Task",
  "assignedTo": "user@example.com"
}
```
- Conexión: `ws://localhost:8080/ws`
- Suscripción: `/topic/user/{userId}`
- El backend envía notificación cuando se asigna una nueva tarea

### 🧪 Quick WebSocket Test (HTML)

1. Crea un archivo llamado `websocket-test.html` con el siguiente contenido:

```html
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Notificador WebSocket</title>
  <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>
</head>
<body>
  <h2>📬 Estás escuchando las notificaciones...</h2>
  <div id="output"></div>
  <script>
    const socket = new SockJS("http://localhost:8080/ws");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {
      console.log("Conectado");
      stompClient.subscribe("/topic/tasks", function (message) {
        const body = message.body;
        document.getElementById("output").innerHTML += "<p>🔔 " + body + "</p>";
      });
    });
  </script>
</body>
</html>

```
2. Abre el archivo en un servidor local, por ejemplo con python:

En la misma carpeta del archivo

```
python3 -m http.server 5500
```

3. Cambia la variable : websocket.allowed-origin por en la que iniciaste el servidor:

```
websocket.allowed-origin=<tu_url> #ejemplo: http://localhost:5500
```

4.Abre Postman y haz una petición `POST` a:

```
<tu url>/api/tasks # ejemplo: http://localhost:8080/api/tasks
```

Con el siguiente JSON en el body (formato `raw` y `application/json`):

```json
{
  "title": "Tarea de prueba",
  "description": "Esto es solo una prueba para el WebSocket",
  "status": "PENDING",
  "assignedUserId": 2
}
```
No olvides incluir el token de login como admin en: 

Authorization: Bearer <token>

## 🧪 Pruebas Unitarias

### Ejecutar tests
```bash
mvn test
```

Incluye:

- `AuthServiceTest`: registro y validación de email
- `TaskServiceTest`: creación de tareas, verificación de notificación y errores

---

## 📈 Monitoreo (Spring Boot Actuator) local

### Endpoints disponibles:
- `GET /actuator/health`
- `GET /actuator/info`

Configuración en `application-dev.properties`:
```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

---

## 🧼 Buenas prácticas

✅ Validaciones con javax.validation y claves i18n (task.title_required, etc.), desacopladas del idioma y listas para internacionalización.

✅ Manejo global de errores con @ControllerAdvice y GlobalExceptionHandler, devolviendo errores estructurados.

✅ Uso de SLF4J para logs limpios y profesionales (logger.info(...), logger.error(...)) en controladores y servicios.

✅ DTOs separados para entrada (TaskRequest) y salida (TaskResponse).

✅ Estructura de capas clara: controllers delgados, servicios con lógica, repositorios inyectados.

✅ Control de acceso con @PreAuthorize y separación de roles (ADMIN, USER) aplicados en endpoints.

✅ Filtros dinámicos con Specification y JpaSpecificationExecutor para búsquedas por título, descripción y estado.

✅ Uso de Pageable, Page<TaskResponse> y ordenamiento (sort, size, page) para respuesta escalable.

✅ Tokens JWT; autenticación sin formLogin, sin httpBasic, solo con Bearer en headers.

✅ Notificaciones en tiempo real vía WebSocket.

✅ Inicialización segura de datos con DatabaseInitializer: crea roles y usuarios si no existen.

---

## 🧠 Autor

**Carlos Andrés Ortiz Peña**  
Senior Fullstack Developer 💻 | Constructor de soluciones 🚀
---