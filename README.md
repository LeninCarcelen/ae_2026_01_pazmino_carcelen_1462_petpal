# PetPal — Sistema de Gestión de Mascotas y Citas Veterinarias

Proyecto Integrador — Arquitectura Empresarial (PUCE, 2026-01)

## Integrantes

- [Carlos_Pazmino]
- [Lenin_Carceñen]

**NRC:** [1462]

## Descripción del proyecto

PetPal es un monorepo de microservicios que gestiona dueños de mascotas, mascotas,
vacunas, citas veterinarias y veterinarios, junto con un microservicio de usuarios
que administra el perfil de negocio de cada identidad autenticada en AWS Cognito.

## Arquitectura

```
                        ┌─────────────────────┐
                        │      Cliente         │
                        │ (Postman / Frontend) │
                        └──────────┬───────────┘
                                   │ Authorization: Bearer <JWT Cognito>
                                   ▼
                        ┌─────────────────────┐
                        │        nginx         │   único puerto publicado
                        │   (API Gateway)       │   (${GATEWAY_PORT}:80)
                        └─────┬───────────┬─────┘
              /users/*        │           │        /petpal/*
                    ┌─────────▼───┐   ┌───▼─────────┐
                    │    users     │   │    petpal    │
                    │  :8081       │   │  :8080       │
                    └──────┬───────┘   └──────┬───────┘
                           │                   │
                    ┌──────▼───────┐   ┌──────▼───────┐
                    │   users-db    │   │  petpal-db    │
                    │  (Postgres)   │   │  (Postgres)   │
                    └──────────────┘   └──────────────┘

                    ┌───────────────────────────┐
                    │        db-explorer          │
                    │   (pgAdmin, puerto propio)  │
                    └───────────────────────────┘
```

- Cada microservicio tiene su propia base de datos Postgres, sin acceso cruzado.
- `nginx` es el único contenedor que publica puerto al host; `users`, `petpal` y
  ambas bases usan `expose` en la red interna de Docker Compose.
- El token JWT de Cognito viaja intacto desde el cliente hasta cada microservicio:
  `nginx` propaga el header `Authorization` (ver `nginx/proxy_headers.conf`), y cada
  microservicio lo valida de forma independiente contra el mismo `issuer-uri` de
  Cognito (`spring-boot-starter-oauth2-resource-server`).
- Comunicación entre microservicios: por ahora `users` y `petpal` son dominios
  independientes que comparten únicamente la identidad de Cognito (mismo
  `cognito:sub` en el token). `petpal` ya tiene configurada la variable de entorno
  `USERS_SERVICE_URL=http://users:8081` (descubrimiento por nombre de servicio de
  Compose) lista para cuando se necesite una llamada síncrona REST propagando el
  mismo `Authorization` recibido — hoy no se usa porque ningún flujo actual la
  requiere.

## Estructura del repositorio

```
ae_2026_01_[apellido1]_[apellido2]_[nrc]_petpal/
├── users/              # microservicio de usuarios (perfil de negocio ligado a Cognito)
├── petpal/              # microservicio de dominio: owners, pets, vaccines, appointments, veterinarians
├── nginx/                # API Gateway
│   ├── nginx.conf
│   └── proxy_headers.conf
├── docker-compose.yml
├── .env.example
└── README.md
```

## Cómo levantar el proyecto

1. Copiar `.env.example` a `.env` y completar los valores reales (credenciales de
   Postgres, `COGNITO_ISSUER_URI`, `COGNITO_USER_POOL_ID`, `COGNITO_REGION`,
   credenciales de pgAdmin).
2. Tener creados y **confirmados** en el User Pool de Cognito al menos dos usuarios
   de prueba, uno por cada rol usado en la autorización (`Administrator`,
   `Veterinarian`/`Hairdresser`/`Clients` según el flujo a probar).
3. Levantar todo:
   ```bash
   docker compose up -d
   docker compose ps   # todos "healthy"; solo nginx con PORTS
   ```
4. Verificar que el gateway responde y que los microservicios NO responden
   directamente desde el host:
   ```bash
   curl http://localhost:${GATEWAY_PORT}/users/api/users/me
   curl http://localhost:${GATEWAY_PORT}/petpal/api/owners
   curl http://localhost:8081/api/users   # debe fallar (puerto no publicado)
   ```
5. Abrir pgAdmin en `http://localhost:${PGADMIN_PORT}` y registrar ambas conexiones
   (`users-db`, `petpal-db`) con las credenciales del `.env`.
6. Importar en Postman `PetPal.postman_collection.json` y el environment
   `PetPal-Local.postman_environment.json`; cargar `access_token` con el JWT de un
   usuario de prueba de Cognito.
7. Dejar corriendo en una terminal aparte:
   ```bash
   docker compose logs -f
   ```

**URL desplegada:** [URL_DE_DESPLIEGUE_SI_APLICA]

## Verificaciones técnicas (autoevaluación)

- [ ] `docker compose up -d` → todos los contenedores en `healthy`
- [ ] `docker compose ps` → solo `nginx` con `PORTS`
- [ ] `curl` a través de nginx responde; puerto interno de los microservicios no
      responde desde el host
- [ ] `nginx.conf` con `upstream`, `proxy_pass`, timeouts y propagación de
      `Authorization`
- [ ] `git log --format='%an %ae' | sort | uniq -c` muestra commits de ambos
      integrantes

## Checklist de rúbrica

| # | Criterio | Estado |
|---|---|---|
| 1 | Monorepo (capas, BD separada, inglés, healthchecks, diagrama) | Completo |
| 2 | Logging BD y de negocio | Completo |
| 3 | Explorador de BD (ambas conexiones) | Completo |
| 4 | `docker compose logs -f` durante la demo | Completo |
| 5 | Nombre de repo correcto, avance 100%, ambos integrantes | Pendiente al momento de subir |
| 6 | Tests de todos los servicios | Completo |
| 7 | Colección de Postman con todos los flujos | Completo |
| 8 | Cognito para autenticación y autorización | Completo |

## Notas de logging

Formato de una línea, ver `logback-spring.xml` de cada servicio:

```
<timestamp> | <LEVEL> | <servicio> | sub=<cognito-sub|anonimo> | <logger> | event=<evento> | msg=<mensaje> | <clave=valor ...>
```

`RequestLoggingFilter` genera las líneas `http.request` / `http.response` de cada
petición; los servicios de dominio agregan eventos de negocio con `LogEvent`
(`event=owner.created`, `event=pet.deleted`, etc.). La auditoría de las entidades
principales (`Owner`, `Pet`, `User`) queda en la tabla `audit_log` de cada base,
con quién (`sub` de Cognito), qué operación, cuándo y los valores antes/después.
