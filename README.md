# Task Management API – NUEVO SPA

**Autor:** Carlos Garrido  
**Email:** carl.garrido.aedo@gmail.com  
**Postulación:** Ingeniero en Informática

CRUD de tareas con **JWT**, **Arquitectura Hexagonal (Ports & Adapters)**, **H2 en memoria** y **Swagger/OpenAPI**.  
Tecnologías: **Java 17**, **Spring Boot 3.4.10**, **Spring Security**, **JPA**, **springdoc-openapi**, **Docker Compose**.

---

## Estructura del repositorio

```
.
├─ RULES.md                     # Reglas originales del desafío
├─ README.md                    # (Este archivo) Guía de la entrega
└─ task-management/             # Proyecto Spring Boot
   ├─ docker-compose.yml
   ├─ openapi.json              # Especificación exportada (springdoc)
   ├─ src/                      # Código (hexagonal)
   ├─ pom.xml
   └─ postman_collection.json   # Colección Postman (HFConsulting)
```

> Nota: La carpeta `task-management/generated/` (stubs de OpenAPI) es **opcional** y está **ignorada** en VCS.

---

## Cómo ejecutar la API (Docker Compose)

> Requiere **Docker** y **Docker Compose**. No necesitas Java/Maven instalados localmente.

1) Abrir terminal en la carpeta del proyecto:
```bash
cd task-management
```

2) Levantar la aplicación:
```bash
docker compose up -d app
docker compose logs -f app
```

3) Endpoints principales:
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
- **H2 Console:** `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:taskdb`
  - Usuario: `sa`
  - Clave: `password`

> Esta entrega usa **H2 en memoria**.

---

## Autenticación (JWT)

### Usuario precargado
```
username: demo
password: demo123
```

### Flujo
1. **Login**: `POST /api/auth/login`  
   Body:
   ```json
   { "username": "demo", "password": "demo123" }
   ```
   Respuesta:
   ```json
   { "token": "<JWT>" }
   ```

2. Usar el header `Authorization: Bearer <JWT>` en los endpoints de tareas.  
   En Postman esto se maneja automáticamente mediante la variable `TOKEN` (ver abajo).

---

## Colección Postman (HFConsulting)

Se incluye `task-management/postman_collection.json` con las siguientes requests:
- **Crear Usuario** (registro)
- **Login Usuario** (guarda token automáticamente en la variable `TOKEN`)
- **Create Task**
- **Listar Mis Tareas**
- **Obtener por Id Tarea** *(usa por defecto id=1; puedes cambiarlo por el id real de la tarea que crees)*
- **Actualizar Tarea** *(id=1 por defecto)*
- **Completar Tarea** *(id=1 por defecto)*
- **Elimiar Tarea** *(id=1 por defecto)*

### Variables de colección
La colección define dos variables:
- `BASE_URL` → debe ser `http://localhost:8080`
- `TOKEN` → se completa sola al hacer **Login Usuario**

> Si por alguna razón el token no se guarda, puedes pegarlo manualmente en `TOKEN` o en el header `Authorization` de cada request como `Bearer <TOKEN>`.

### Uso paso a paso
1. Importar `task-management/postman_collection.json` en Postman.
2. Abrir la colección **HFConsulting** → pestaña **Variables** y setear `BASE_URL = http://localhost:8080`. Dejar `TOKEN` vacío.
3. Ejecutar **Login Usuario** → el test guarda el JWT en `TOKEN` automáticamente.
4. Ejecutar **Create Task**. Con la respuesta de **Listar Mis Tareas** podrás ver el `id` de la tarea.
5. Para las requests con `id` fijo (1), actualiza el path con el id real según corresponda:
   - `GET {{BASE_URL}}/api/tasks/{id}`
   - `PUT {{BASE_URL}}/api/tasks/{id}`
   - `PUT {{BASE_URL}}/api/tasks/{id}/complete`
   - `DELETE {{BASE_URL}}/api/tasks/{id}`

---

## OpenAPI / API First

### Especificación incluida
- Archivo: `task-management/openapi.json` (exportado desde springdoc).

### Validar la especificación (Docker)
```bash
cd task-management
docker compose run --rm openapi-validator
```
Salida esperada:
```
Validating spec (/local/openapi.json)
No validation issues detected.
```

### (Opcional) Generar **stubs** de servidor (demostrativo)
Genera un **esqueleto** a `./task-management/generated/` (no usado en runtime):
```bash
docker compose run --rm openapi-stubs
```
> Los stubs no se copian a `src/` para evitar conflictos. Se mantienen fuera y se ignoran en VCS.

---

## Arquitectura (Hexagonal – Ports & Adapters)

- **application/**
  - *input ports* (casos de uso): `TaskManagementInputPort`
  - *output ports* (persistencia): `TaskPersistancePort`
- **domain/**
  - Modelos (`Task`, `User`, `TaskStatus`)
  - Servicio de dominio (`TaskService`) — inyecta `userId` del usuario autenticado y aplica reglas (fecha de creación, completado, etc.)
- **infrastructure/**
  - **web** (controllers, DTOs, mappers)
  - **security** (JWT: `JwtProvider`, `JwtAuthenticationFilter`, `SecurityConfig`, `UserDetailsServiceImpl`)
  - **persistence** (adapter JPA: `JpaTaskPersistenceAdapter`, repositorios)
  - **config** (DataSeeder, OpenAPI)

---

## Datos precargados
- **Usuarios**: seeder crea `demo / demo123`.
- **Estados de Tarea** (`status_task`): `PENDIENTE`, `EN_PROGRESO`, `COMPLETADA`.

---

## Tecnologías
- Java 17 · Spring Boot 3.4.10
- Spring Security + JWT (jjwt)
- Spring Data JPA · H2
- springdoc-openapi (Swagger UI)
- Docker Compose

---

## Desarrollo local (opcional, sin Docker)
Si prefieres ejecutar con Maven local:
```bash
cd task-management
mvn spring-boot:run
```

---

## Notas de evaluación
- **Seguridad:** `/api/tasks/**` protegido con JWT y filtrado por `userId`.
- **Documentación:** Swagger UI + `openapi.json` versionado y validable con Docker.
- **API First (intención):** se incluye la especificación y generación de stubs (excluidos de `src/`).
- **Arquitectura:** separación por capas con Ports & Adapters.
- **Datos:** H2 en memoria con usuario y estados precargados.
