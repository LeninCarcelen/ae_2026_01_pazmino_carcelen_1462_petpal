# PetPal

API REST en Spring Boot + Kotlin + Gradle, con autenticación vía AWS Cognito y dominio de
gestión de mascotas: **Owner**, **Pet**, **Vaccine** y **Appointment**.

Estructura y estilo de código adaptados al material de clase (PUCE - "parte dos clase7"):
entidades `open class` con valores por defecto, mappers como funciones de extensión,
servicios sin interfaz con logging, controllers que devuelven el DTO directo, y excepciones
simples manejadas por un `GlobalExceptionHandler` centralizado.

## Estructura de paquetes (`com.petpal`)

```
controllers/  -> Endpoints REST (Owner, Pet, Vaccine, Appointment)
dto/          -> Request/Response por entidad (OwnerDto, PetDto, VaccineDto, AppointmentDto)
entities/     -> Entidades JPA (Owner, Pet, Vaccine, Appointment)
exceptions/   -> Excepciones por entidad + GlobalExceptionHandler
mappers/      -> Funciones de extensión toEntity()/toResponse() por entidad
repositories/ -> Interfaces JpaRepository
security/     -> Validación de JWT de Cognito (sin cambios, sigue igual que antes)
services/     -> Lógica de negocio, una clase @Service por entidad, con logger
```

## Modelo de dominio

- **Owner** (dueño): `name`, `email` (único), `phone`.
- **Pet** (mascota): `name`, `species`, `breed`, `birthDate`, relación `ManyToOne` a `Owner`.
- **Vaccine** (vacuna): `name`, `dateApplied`, `nextDueDate`, relación `ManyToOne` a `Pet`.
  Regla de dominio: `nextDueDate` no puede ser anterior a `dateApplied`.
- **Appointment** (cita): `date`, `reason`, `status` (enum `AppointmentStatus`: `SCHEDULED`,
  `COMPLETED`, `CANCELLED`), relación `ManyToOne` a `Pet` y relación **`ManyToMany`** a
  `Veterinarian` (una cita puede tener varios veterinarios asignados, un veterinario puede
  atender varias citas), vía tabla intermedia `appointment_veterinarian`.
- **Veterinarian** (veterinario): `name`, `specialty`.

## Validaciones y manejo de errores

Todos los `*Request` usan Bean Validation (`@NotBlank`, `@Email`, `@Positive`, etc). Un body
inválido responde `400` con el detalle de los campos vía `GlobalExceptionHandler`
(`MethodArgumentNotValidException` / `ConstraintViolationException`). Reglas de dominio propias
(fecha de vacuna, estado de cita inválido) lanzan `DomainValidationException` → `400`.

## Autenticación y autorización

Roles esperados en el claim `cognito:groups` del JWT (nombres de grupo existentes en el
User Pool, sin prefijo `ROLE_`): `Administrator`, `Veterinarian`, `Hairdresser`, `Clients`.

| Rol             | Permisos                                                         |
|-----------------|--------------------------------------------------------------------|
| `Administrator` | Acceso total (incluye `DELETE`)                                   |
| `Veterinarian`  | `GET`, `POST`, `PUT`, `PATCH` (no `DELETE`)                        |
| `Hairdresser`   | Mismos permisos que `Veterinarian`                                 |
| `Clients`       | Solo `GET`                                                         |

Sin token → `401`. Con token pero sin el rol adecuado → `403`.

## Autenticación: AWS Cognito

**No cambió respecto a la versión anterior.** El login lo maneja el frontend/app vía Cognito
Hosted UI o el SDK (Amplify, `amazon-cognito-identity-js`, etc). El backend es **Resource
Server**: solo valida el JWT que Cognito ya emitió.

Antes de correr el proyecto, reemplaza en `application.yml`:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://cognito-idp.<REGION>.amazonaws.com/<USER_POOL_ID>
```

Todos los endpoints de negocio (`/api/owners`, `/api/pets`, `/api/vaccines`, `/api/appointments`)
requieren `Authorization: Bearer <token>`.

## Endpoints

| Método | Ruta                              | Descripción                          |
|--------|-----------------------------------|---------------------------------------|
| POST   | /api/owners                       | Crear owner                           |
| GET    | /api/owners                       | Listar owners                         |
| GET    | /api/owners/{id}                  | Owner por id                          |
| DELETE | /api/owners/{id}                  | Eliminar owner                        |
| POST   | /api/pets                         | Crear pet (requiere `ownerId`)        |
| GET    | /api/pets?ownerId={id}            | Listar pets (opcional: filtrar por owner) |
| GET    | /api/pets/{id}                    | Pet por id                            |
| PUT    | /api/pets/{id}                    | Actualizar pet                        |
| DELETE | /api/pets/{id}                    | Eliminar pet                          |
| POST   | /api/vaccines                     | Registrar vacuna (requiere `petId`)   |
| GET    | /api/vaccines?petId={id}          | Listar vacunas (opcional: filtrar por pet) |
| GET    | /api/vaccines/{id}                | Vacuna por id                         |
| DELETE | /api/vaccines/{id}                | Eliminar vacuna                       |
| POST   | /api/appointments                 | Crear cita (requiere `petId`)         |
| GET    | /api/appointments?petId={id}      | Listar citas (opcional: filtrar por pet) |
| GET    | /api/appointments/{id}            | Cita por id                           |
| PATCH  | /api/appointments/{id}/status     | Cambiar status (`?status=COMPLETED`)  |
| DELETE | /api/appointments/{id}            | Eliminar cita                         |
| POST   | /api/veterinarians                | Crear veterinario                     |
| GET    | /api/veterinarians                | Listar veterinarios                   |
| GET    | /api/veterinarians/{id}           | Veterinario por id                    |
| DELETE | /api/veterinarians/{id}           | Eliminar veterinario                  |

Documentación Swagger en `/swagger-ui.html` una vez levantado el proyecto.

## Cómo abrir el proyecto

**Recomendado: IntelliJ IDEA**
1. Abrir la carpeta `petpal` como proyecto Gradle.
2. IntelliJ descarga automáticamente el Gradle wrapper (`gradle-wrapper.jar` no viene incluido en este ZIP).
3. Esperar a que sincronice dependencias.
4. Ejecutar `PetpalApplication.kt`.

**Alternativa: línea de comandos**
Si tienes Gradle instalado localmente, genera el wrapper real antes de usar `./gradlew`:
```powershell
gradle wrapper --gradle-version 8.8
./gradlew bootRun
```

## Base de datos

Por defecto corre con H2 en memoria (consola en `http://localhost:8080/h2-console`,
JDBC URL `jdbc:h2:mem:petpal`, usuario `sa`, sin password). Tablas: `owner`, `pet`, `vaccine`,
`appointment`.

Para usar Postgres/Neon, cambia en `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://<host>/<db>?sslmode=require
    driver-class-name: org.postgresql.Driver
    username: <usuario>
    password: <password>
```

## Antes de producción

- Confirma que el `issuer-uri` de Cognito apunte al User Pool correcto (no dejes el placeholder).
- Configura `ddl-auto` en `validate` en vez de `update`.
- Revisa CORS si el frontend corre en otro origen.
