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

### Usuarios
- **POST /auth/login**  
  **Descripción:** Autentica al usuario. Se envían las credenciales (username y password) y, de ser correctas, se devuelve un token JWT para autorizar futuras solicitudes.

- **POST /auth/register**  
  **Descripción:** Permite el registro de un nuevo usuario. Se envían los datos necesarios (username, password, etc.) y se almacena el usuario con la contraseña hasheada.

- **GET /usuarios**  
  **Descripción:** Devuelve la lista de todos los usuarios (getAll). Este endpoint suele estar restringido a usuarios con rol ADMIN.

- **GET /usuarios/{username}**  
  **Descripción:** Devuelve la información de un usuario específico (get).

- **PUT /usuarios/{username}**  
  **Descripción:** Actualiza la información de un usuario (update). El usuario podrá actualizar sus propios datos o, en el caso de un ADMIN, actualizar la información de cualquier usuario.

- **DELETE /usuarios/{username}**  
  **Descripción:** Elimina un usuario. (Este endpoint se puede implementar para administración, si es necesario).

### Tareas
- **GET /tareas**  
  **Descripción:**
  - Para usuario con rol USER: Devuelve únicamente las tareas asociadas al usuario autenticado (getAll).
  - Para usuario con rol ADMIN: Devuelve todas las tareas de la aplicación.

- **GET /tareas/{codigo}**  
  **Descripción:** Devuelve una tarea específica (get).

- **POST /tareas**  
  **Descripción:**
  - Para usuario con rol USER: Permite dar de alta una nueva tarea que se asociará automáticamente al usuario autenticado.
  - Para usuario con rol ADMIN: Permite crear una tarea y asignarla a cualquier usuario especificado en la solicitud.

- **PUT /tareas/{codigo}**  
  **Descripción:** Actualiza la información de una tarea (update). Se pueden modificar campos como título, descripción o estado (excepto la acción de “completar”, que tiene un endpoint específico).

- **PUT /tareas/{codigo}/completar**  
  **Descripción:** Marca una tarea como completada.  
  **Restricción:** El usuario solo puede completar sus propias tareas, a menos que sea administrador.

- **DELETE /tareas/{codigo}**  
  **Descripción:** Elimina una tarea.  
  **Restricción:** El usuario solo puede eliminar sus propias tareas, mientras que un administrador puede eliminar cualquier tarea.

### Direcciones
- **GET /direcciones**  
  **Descripción:** Devuelve la lista de todas las direcciones (getAll). La visibilidad de este endpoint dependerá de la política de acceso (por ejemplo, solo ADMIN o el usuario propietario).

- **GET /direcciones/{codigo}**  
  **Descripción:** Devuelve una dirección específica (get).

- **GET /direcciones/usuario/{username}**  
  **Descripción:** Devuelve la dirección asociada a un usuario determinado. (Alternativa para obtener la dirección por el id del usuario.)

- **POST /direcciones**  
  **Descripción:** Permite dar de alta una dirección para un usuario. La dirección se asocia al usuario indicado.

- **PUT /direcciones/{codigo}**  
  **Descripción:** Actualiza la información de una dirección (update).

- **DELETE /direcciones/{codigo}**  
  **Descripción:** Elimina una dirección. (Este endpoint se puede implementar para administración o para que el propio usuario elimine su dirección.)

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
  Se devuelve cuando el usuario autenticado intenta acceder a un recurso o realizar una acción para la cual no tiene permisos (por ejemplo, un usuario intentando eliminar una tarea de otro usuario)

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

---

## 6. PRUEBAS GESTIÓN USUARIOS

### Registro de usuarios

- Pruebas de ejemplo de registrar un usuario de exitosamente.

![Parte2_1.png](src/main/resources/capturasparte2/Parte2_1.png)

- Pruebas de ejemplo de error al registrar un usuario con el mismo username.

![RegisterFallo1.png](src/main/resources/capturasparte2/RegisterFallo1.png)

- Pruebas de ejemplo de error al registrar un usuario sin validar la contraseña correctamente.

![RegisterFallo2.png](src/main/resources/capturasparte2/RegisterFallo2.png)

### Login de usuarios

- Se logea un usuario correctamente, lo que hace que devuelva un token.

![Parte2_2.png](src/main/resources/capturasparte2/Parte2_2.png)

- Se intenta logear con una contraseña incorrecta.

![LoginFallo1.png](src/main/resources/capturasparte2/LoginFallo1.png)

- Se intenta logear con un usuario no registrado en la BBDD.

![LoginFallo2.png](src/main/resources/capturasparte2/LoginFallo2.png)

### Gestión de usuarios con interfaz

- Se introducen los datos para registar un usuario en la interfaz y le damos a register.

![RegisterInterfaz1.png](src/main/resources/capturasparte2/RegisterInterfaz1.png)

- Vamos a la BBDD MongoDB y comprobamos que el usuario se ha registrado correctamente.

![RegisterInterfaz2.png](src/main/resources/capturasparte2/RegisterInterfaz2.png)

- Introducimos en la interfaz los datos de un usuario registrado, al hacer esto la interfaz nos devolverá un token.

![loginInterfaz1.png](src/main/resources/capturasparte2/loginInterfaz1.png)

---

## 7. PRUEBAS GESTIÓN TAREAS

## Pruebas usuarios

### GET /usuarios

![obtenerusuarios1.png](src/main/resources/capturasparte3/obtenerusuarios1.png)

- Método para que un administrador pueda obtener todos los usuarios.

![obtenerusuarios2.png](src/main/resources/capturasparte3/obtenerusuarios2.png)

- Excepción cuando un usuario intenta obtener todos los usuarios.

### GET /usuarios/{username}

![usuarioId1.png](src/main/resources/capturasparte3/usuarioId1.png)

- Método para obtener un usuario por su username.

![usuarioId2.png](src/main/resources/capturasparte3/usuarioId2.png)

- Excepción que salta cuando un usuario intenta obtener otro usuario que no es el suyo.

![usuarioId3.png](src/main/resources/capturasparte3/usuarioId3.png)

- Excepción que salta cuando no se encuentra el usuario solicitado.

### PUT /usuarios/{username}

![actualizarusuario1.png](src/main/resources/capturasparte3/actualizarusuario1.png)

- Se actualiza la contraseña y el email del usuario indicado por su username.

![actualizarusuario3.png](src/main/resources/capturasparte3/actualizarusuario3.png)

- Excepción que salta cuando un usuario intenta obtener otro usuario que no es el suyo.

![actualizarusuario2.png](src/main/resources/capturasparte3/actualizarusuario2.png)

- Excepción que salta cuando no se encuentra el usuario solicitado.

### DELETE /usuarios/{username}

![eliminarusuario1.png](src/main/resources/capturasparte3/eliminarusuario1.png)

- Método para eliminar un usuario por su username.

![eliminarusuario2.png](src/main/resources/capturasparte3/eliminarusuario2.png)

- Excepción que salta cuando un usuario intenta eliminar otro usuario que no es el suyo.

![eliminarusuario3.png](src/main/resources/capturasparte3/eliminarusuario3.png)

- Excepción que salta cuando no se encuentra el usuario solicitado.

## Pruebas tareas

### GET /tareas

![obtenertareas1.png](src/main/resources/capturasparte3/obtenertareas1.png)

- Método para que un administrador pueda obtener todas tareas de todos los usuarios.

![obtenertareas2.png](src/main/resources/capturasparte3/obtenertareas2.png)

- Método para que un usuario pueda obtener todas sus tareas.

### GET /tareas/{codigo}

![tareaId1.png](src/main/resources/capturasparte3/tareaId1.png)

- Método para que un administrador pueda obtener cualquier tarea de cualquier usuario.

![tareaId2.png](src/main/resources/capturasparte3/tareaId2.png)

- Método para que un usuario pueda obtener una tarea asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![tareaId3.png](src/main/resources/capturasparte3/tareaId3.png)

- Excepción que salta cuando un usuario intenta obtener una tarea de un usuario que no es el suyo.

![tareaId4.png](src/main/resources/capturasparte3/tareaId4.png)

- Excepción que salta cuando no se encuentra la tarea solicitada.

### POST /tareas

![creartarea1.png](src/main/resources/capturasparte3/creartarea1.png)

- Método para que un administrador pueda crear una tarea asociada a cualquier usuario asignandola a su username.

![creartarea2.png](src/main/resources/capturasparte3/creartarea2.png)

- Método para que un usuario pueda crear una tarea asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![creartarea3.png](src/main/resources/capturasparte3/creartarea3.png)

- Excepción que salta cuando se intenta crear una tarea sin un token válido.

![creartarea4.png](src/main/resources/capturasparte3/creartarea4.png)

- Excepción que salta cuando se intenta craer una tarea con un código ya existente en la BBDD.

### PUT /tareas/{codigo}

![actualizartareaid1.png](src/main/resources/capturasparte3/actualizartareaid1.png)

- Método para que un administrador pueda actualizar una tarea asociada a cualquier usuario mediante su username.

![actualizartareaid2.png](src/main/resources/capturasparte3/actualizartareaid2.png)

- Método para que un usuario pueda actualizar una tarea asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![actualizartareaid3.png](src/main/resources/capturasparte3/actualizartareaid3.png)

- Excepción que salta cuando un usuario intenta obtener una tarea de un usuario que no es el suyo.

![actualizartareaid4.png](src/main/resources/capturasparte3/actualizartareaid4.png)

- Excepción que salta cuando no se encuentra la tarea solicitada.

### PUT /tareas/{codigo}/completar

![completartarea1.png](src/main/resources/capturasparte3/completartarea1.png)

- Método para que un administrador pueda marcar como completada una tarea asociada a cualquier usuario mediante su username.

![completartarea2.png](src/main/resources/capturasparte3/completartarea2.png)

- Método para que un usuario pueda marcar como completada una tarea asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![completartarea3.png](src/main/resources/capturasparte3/completartarea3.png)

- Excepción que salta cuando un usuario intenta marcar como completada una tarea de un usuario que no es el suyo.

![completartarea4.png](src/main/resources/capturasparte3/completartarea4.png)

- Excepción que salta cuando no se encuentra la tarea solicitada.

### DELETE /tareas/{codigo}

![eliminartarea1.png](src/main/resources/capturasparte3/eliminartarea1.png)

- Método para que un administrador pueda eliminar una tarea asociada a cualquier usuario mediante su username.

![eliminartarea2.png](src/main/resources/capturasparte3/eliminartarea2.png)

- Método para que un usuario pueda eliminar una tarea asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![eliminartarea3.png](src/main/resources/capturasparte3/eliminartarea3.png)

- Excepción que salta cuando un usuario intenta eliminar una tarea de un usuario que no es el suyo.

![eliminartarea4.png](src/main/resources/capturasparte3/eliminartarea4.png)

- Excepción que salta cuando no se encuentra la tarea solicitada.

## Pruebas direcciones

### GET /direcciones

![obtenerdirecciones1.png](src/main/resources/capturasparte3/obtenerdirecciones1.png)

- Método para que un administrador pueda obtener todas las direcciones de todos los usuarios.

![obtenerdirecciones2.png](src/main/resources/capturasparte3/obtenerdirecciones2.png)

- Método para que un usuario pueda obtener su dirección.

![obtenerdirecciones3.png](src/main/resources/capturasparte3/obtenerdirecciones3.png)

- Excepción que salta cuando un usuario tiene más de una dirección asociada.

### GET /direcciones/{codigo}

![direccionId1.png](src/main/resources/capturasparte3/direccionId1.png)

- Método para que un administrador pueda obtener cualquier dirección de cualquier usuario.

![direccionId2.png](src/main/resources/capturasparte3/direccionId2.png)

- Método para que un usuario pueda obtener la dirección asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![direccionId3.png](src/main/resources/capturasparte3/direccionId3.png)

- Excepción que salta cuando un usuario intenta obtener la dirección de un usuario que no es el suyo.

![direccionId4.png](src/main/resources/capturasparte3/direccionId4.png)

- Excepción que salta cuando no se encuentra la dirección solicitada.

### GET /direcciones/usuario/{username}

![direccionusuario1.png](src/main/resources/capturasparte3/direccionusuario1.png)

- Método para que un administrador pueda obtener cualquier dirección de cualquier usuario usando el username como parámetro de búsqueda.

![direccionusuario2.png](src/main/resources/capturasparte3/direccionusuario2.png)

- Método para que un usuario pueda obtener la dirección asociada a si mismo usando el username como parámetro de búsqueda (se ignora el contenido del username, el token asigna al usuario).

![direccionusuario3.png](src/main/resources/capturasparte3/direccionusuario3.png)

- Excepción que salta cuando un usuario intenta obtener la dirección de un usuario que no es el suyo.

![direccionusuario4.png](src/main/resources/capturasparte3/direccionusuario4.png)

- Excepción que salta cuando no se encuentra la dirección solicitada.

### POST /direcciones

![creardireccion1.png](src/main/resources/capturasparte3/creardireccion1.png)

- Método para que un administrador pueda crear una dirección asociada a cualquier usuario asignandola a su username.

![creardireccion2.png](src/main/resources/capturasparte3/creardireccion2.png)

- Método para que un usuario pueda crear una dirección asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![creardireccion3.png](src/main/resources/capturasparte3/creardireccion3.png)

- Excepción que salta cuando se intenta crear una dirección sin un token válido.

![creardireccion4.png](src/main/resources/capturasparte3/creardireccion4.png)

- Excepción que salta cuando se intenta craer una dirección con un código ya existente en la BBDD.

### PUT /direcciones/{codigo}

![actualizardireccion1.png](src/main/resources/capturasparte3/actualizardireccion1.png)

- Método para que un administrador pueda actualizar cualquier dirección de cualquier usuario.

![actualizardireccion2.png](src/main/resources/capturasparte3/actualizardireccion2.png)

- Método para que un usuario pueda actualizar la dirección asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![actualizardireccion3.png](src/main/resources/capturasparte3/actualizardireccion3.png)

- Excepción que salta cuando un usuario intenta actualizar la dirección de un usuario que no es el suyo.

![actualizardireccion4.png](src/main/resources/capturasparte3/actualizardireccion4.png)

- Excepción que salta cuando no se encuentra la dirección solicitada.

### DELETE /direcciones/{codigo}

![eliminardireccion1.png](src/main/resources/capturasparte3/eliminardireccion1.png)

- Método para que un administrador pueda eliminar cualquier dirección de cualquier usuario.

![eliminardireccion2.png](src/main/resources/capturasparte3/eliminardireccion2.png)

- Método para que un usuario pueda eliminar la dirección asociada a si mismo (se ignora el contenido del username, el token asigna al usuario).

![eliminardireccion3.png](src/main/resources/capturasparte3/eliminardireccion3.png)

- Excepción que salta cuando un usuario intenta eliminar la dirección de un usuario que no es el suyo.

![eliminardireccion4.png](src/main/resources/capturasparte3/eliminardireccion4.png)

- Excepción que salta cuando no se encuentra la dirección solicitada.

---