# PetPal — Frontend (React + Vite + Ionic React)

Migración 1:1 del frontend Ionic/Angular a **React + Vite + Ionic React**, manteniendo la misma
arquitectura, endpoints y reglas de negocio. Consume el backend **PetPal** (`users` + `petpal`
microservices, Spring Boot + Kotlin, autenticación AWS Cognito) sin ningún cambio en el backend.

## Requisitos

- Node.js 18+ y npm
- El backend (`petpal_monorepo`) corriendo vía `docker compose up -d` (gateway Nginx en `http://localhost:9090` por defecto, ver `.env.example` → `GATEWAY_PORT`)

## Instalación

```bash
npm install
```

## Configuración

Antes de correr la app, edita `src/environments/environment.ts` con tus valores reales de Cognito y del gateway (hay un bloque `dev` y otro `prod`, seleccionados automáticamente según `vite dev` / `vite build`):

```ts
const dev: Environment = {
  production: false,
  apiBaseUrl: 'http://localhost:9090',       // GATEWAY_PORT de docker-compose.yml
  cognito: {
    domain: 'https://<tu-dominio>.auth.<region>.amazoncognito.com',
    clientId: '<tu-client-id>',
    redirectUri: 'http://localhost:8100/auth/callback',
    scope: 'openid email',
    region: '<tu-region>',
    userPoolId: '<tu-user-pool-id>'
  }
};
```

**Importante:** registra `http://localhost:8100/auth/callback` (y el equivalente de producción) como **Allowed callback URL** en el App Client de tu User Pool de Cognito (Cognito console → App integration → App client → Hosted UI), igual que se hizo con `https://oauth.pstmn.io/v1/callback` para Postman.

## Levantar la app

```bash
npm run dev
```

Esto abre la app en `http://localhost:8100` (puerto fijado en `vite.config.ts` para que coincida con el `redirectUri` configurado en Cognito). El flujo de login redirige al Hosted UI de Cognito y regresa a `/auth/callback`, replicando exactamente el flujo manual que se usa en Postman (`Auth > Exchange code for token`).

## Build

```bash
npm run build     # genera dist/
npm run preview   # sirve el build de producción localmente
```

## Roles soportados

| Rol interno | Grupo en Cognito | Permisos en la UI |
|---|---|---|
| ADMIN | `Administrator` | CRUD completo en todos los recursos, incluye eliminar y gestión de usuarios |
| VET | `Veterinarian` | Crear/editar Owners, Pets, Vaccines, Veterinarians, Appointments (sin eliminar) |
| HAIRDRESSER | `Hairdresser` | Mismos permisos de creación/edición que VET (sin eliminar) — según `SecurityConfig.kt` |
| OWNER | `Clients` | Solo lectura en todos los recursos |

La UI oculta botones según el rol (`useAuth().hasRole(...)`), pero **la autorización real vive en el backend** (`SecurityConfig.kt`) — ocultar un botón es solo UX, no seguridad. Si el token no tiene el rol correcto, el backend responde `403 Forbidden` igual, tal como se validó en la carpeta de Negativos de Postman.

## Estructura

```
src/
├── core/
│   ├── models/          # Interfaces TS calcadas de los DTO.kt del backend
│   ├── services/         # Un módulo por recurso (owner, pet, vaccine, veterinarian, appointment, user)
│   ├── auth/              # authStore (singleton, equivalente a AuthService) + AuthContext (hook useAuth)
│   ├── guards/            # RequireAuth (sesión activa) y RequireRole (rol requerido por ruta)
│   └── http.ts            # wrapper de fetch que agrega Bearer token a cada request (interceptor)
├── pages/
│   ├── login/              # Redirección a Cognito Hosted UI + página de callback
│   ├── profile/            # Perfil propio + auto-registro (POST /api/users)
│   ├── owners/ pets/ vaccines/ veterinarians/ appointments/
│   └── users/              # Solo ADMIN (protegida por RequireRole)
├── tabs/                    # Navegación principal (tabs condicionados por rol)
├── environments/            # Config dev/prod (equivalente a environment.ts / environment.prod.ts)
└── App.tsx                  # Setup de Ionic React + rutas (equivalente a app.routes.ts + main.ts)
```

## Notas sobre endpoints usados

Todos los servicios apuntan al gateway (`environment.apiBaseUrl`) con los mismos paths validados en la colección de Postman:

- `GET/POST /petpal/api/owners`, `DELETE /petpal/api/owners/{id}`
- `GET/POST/PUT/DELETE /petpal/api/pets`, filtro `?ownerId=`
- `GET/POST/DELETE /petpal/api/vaccines`, filtro `?petId=`
- `GET/POST/DELETE /petpal/api/veterinarians`
- `GET/POST/DELETE /petpal/api/appointments`, filtro `?petId=`, `PATCH /{id}/status?status=`
- `GET/POST/PUT/DELETE /users/api/users`, `GET /users/api/users/me`

## Empaquetado móvil (Capacitor)

```bash
npm run build
npx cap add android
npx cap add ios
npx cap sync
```

`capacitor.config.ts` ya apunta `webDir` a `dist` (carpeta de salida de Vite).

## Pendiente / próximos pasos

- Manejo de `refresh_token` para renovar sesión sin volver a pasar por el Hosted UI cada ~3 horas.
- Tests de componentes (actualmente el proyecto no incluye specs).
- Ajustar el bloque `prod` de `src/environments/environment.ts` con las URLs reales de producción antes del build final.
