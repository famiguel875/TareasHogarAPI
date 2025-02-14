# API REST Segura - TareasHogarAPI

## 1. Información General

### a. Nombre del Proyecto
API REST Segura - TareasHogarAPI

### b. Descripción de los Documentos (Entidades) y sus Campos

#### Usuario
- **username:** Cadena única que identifica al usuario.
- **password:** Contraseña hasheada.
- **roles:** Rol(es) asignados al usuario (por ejemplo, USER o ADMIN).
- **Campos opcionales:** email, fecha de registro, etc.

#### Tarea
- **id:** Identificador único de la tarea.
- **titulo:** Título breve de la tarea.
- **descripcion:** Descripción detallada de la tarea.
- **estado:** Estado de la tarea (por ejemplo, PENDIENTE o COMPLETADA).
- **usuario:** Referencia al usuario propietario de la tarea.

#### Dirección
- **id:** Identificador único de la dirección.
- **calle:** Nombre de la calle.
- **numero:** Número de la dirección.
- **ciudad:** Ciudad.
- **codigoPostal:** Código postal.
- **usuario:** Referencia al usuario al que pertenece la dirección.

---

## 2. Endpoints y Descripción de Cada uno

### Autenticación

- **POST /api/auth/login**  
  **Descripción:** Autentica al usuario. Se envían las credenciales (username y password) y, de ser correctas, se devuelve un token JWT para autorizar futuras solicitudes.

- **POST /api/auth/register**  
  **Descripción:** Permite el registro de un nuevo usuario. Se envían los datos necesarios (username, password, etc.) y se almacena el usuario con la contraseña hasheada.

### Tareas

- **GET /api/tareas**  
  **Descripción:**
  - Usuario con rol USER: Devuelve únicamente las tareas asociadas al usuario autenticado.
  - Usuario con rol ADMIN: Devuelve todas las tareas de la aplicación.

- **POST /api/tareas**  
  **Descripción:**
  - Usuario con rol USER: Permite dar de alta una nueva tarea que se asociará automáticamente al usuario autenticado.
  - Usuario con rol ADMIN: Permite crear una tarea y asignarla a cualquier usuario especificado en la solicitud.

- **PUT /api/tareas/{id}/completar**  
  **Descripción:** Marca una tarea como completada.  
  **Restricción:** El usuario solo puede completar sus propias tareas, a menos que sea administrador.

- **DELETE /api/tareas/{id}**  
  **Descripción:** Elimina una tarea.  
  **Restricción:** El usuario solo puede eliminar sus propias tareas, mientras que un administrador puede eliminar cualquier tarea.

### Usuarios (para administración)

- **GET /api/usuarios/{id}**  
  **Descripción:** Devuelve la información de un usuario específico. Este endpoint está destinado para funciones administrativas o de consulta detallada.

### Direcciones

- **GET /api/direcciones/{usuarioId}**  
  **Descripción:** Devuelve la dirección asociada a un usuario determinado.

- **POST /api/direcciones**  
  **Descripción:** Permite dar de alta una dirección para un usuario. La dirección se asocia al usuario indicado.

---

## 3. Lógica de Negocio

### Registro y Autenticación
- Se implementa mediante endpoints dedicados.
- Las contraseñas se almacenan de forma hasheada.
- Tras una autenticación exitosa, se genera y devuelve un token JWT para mantener la sesión.

### Gestión de Tareas
- Los usuarios pueden crear, consultar, marcar como completadas y eliminar sus propias tareas.
- Los administradores tienen la capacidad de ver y eliminar cualquier tarea, y pueden asignar tareas a cualquier usuario.
- Al crear una tarea, se valida que el usuario autenticado tenga los permisos adecuados (solo puede asignar tareas a sí mismo si es usuario, o a cualquier usuario si es administrador).

### Validaciones y Restricciones
- Se comprueba que los usuarios no puedan manipular recursos que no les pertenecen.
- Las solicitudes se validan tanto a nivel de datos (por ejemplo, formatos correctos) como a nivel de permisos.

---

## 4. Manejo de Excepciones y Códigos de Estado

- **400 Bad Request:**  
  Se retorna cuando la solicitud tiene errores en los datos enviados o está mal formada.

- **401 Unauthorized:**  
  Se utiliza cuando el usuario no está autenticado o el token JWT es inválido o ha expirado.

- **403 Forbidden:**  
  Se devuelve cuando el usuario autenticado intenta acceder a un recurso o realizar una acción para la cual no tiene permisos (por ejemplo, un usuario intentando eliminar una tarea de otro usuario).

- **404 Not Found:**  
  Se emplea cuando se solicita un recurso que no existe (por ejemplo, una tarea o usuario inexistente).

- **500 Internal Server Error:**  
  Se utiliza para errores inesperados en el servidor.

---

## 5. Restricciones de Seguridad

### Autenticación y Autorización
- Se utiliza Spring Security para proteger los endpoints de la API.
- Se implementa JWT para gestionar la sesión y verificar la identidad del usuario en cada solicitud.
- Se utiliza un esquema de cifrado asimétrico (clave pública/privada) para asegurar la comunicación y la verificación de tokens.

### Control de Acceso a Endpoints
- Los endpoints de autenticación (`/api/auth/login` y `/api/auth/register`) son accesibles sin autenticación.
- Todos los demás endpoints requieren un token JWT válido.
- Los usuarios con rol USER solo pueden acceder y modificar sus propios recursos (tareas y direcciones).
- Los usuarios con rol ADMIN tienen permisos para acceder y modificar todos los recursos.

### Validación de Datos y Auditoría
- Se implementan validaciones en el servidor para asegurar la integridad y formato correcto de los datos.
- Se registran los intentos de acceso no autorizado y se lleva un registro de auditoría para el análisis y seguimiento de posibles incidencias de seguridad.
