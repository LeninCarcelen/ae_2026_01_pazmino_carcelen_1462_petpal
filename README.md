# PetPal — Fullstack (backend + frontend)

Este repo une el backend (`backend/`, monorepo Spring Boot/Kotlin sin tocar su lógica)
con el frontend (`frontend/`, React + Vite + Ionic React). El frontend ya estaba
diseñado para consumir el backend a través del gateway Nginx, así que **no se modificó
ningún microservicio Kotlin**. El único cambio hecho en el backend fue agregar CORS al
gateway Nginx (`backend/nginx/`), necesario porque el navegador corre el frontend en un
origen distinto (`http://localhost:8100`) al del gateway (`http://localhost:9090`).

## Qué se tocó exactamente en el backend

- `backend/nginx/nginx.conf`: se agregó un bloque `map` para origenes permitidos y un
  `include /etc/nginx/cors_headers.conf;` + manejo de `OPTIONS` en cada `location`.
- `backend/nginx/cors_headers.conf` (archivo nuevo): headers `Access-Control-*`.
- `backend/docker-compose.yml`: se agregó el volumen para montar `cors_headers.conf`
  dentro del contenedor `nginx`.

Nada de `users/src`, `petpal/src`, los `Dockerfile` ni las bases de datos fue tocado.
Si en algún momento quieres verificar que el backend sigue intacto, compara
`backend/` contra el zip original: solo esos 3 archivos cambian.

## Cómo levantar todo

1. Backend:
   ```bash
   cd backend
   cp .env.example .env   # y completa credenciales reales de Cognito/Postgres
   docker compose up -d --build
   docker compose ps      # todos "healthy", solo nginx con PORTS (9090)
   ```

2. Frontend:
   ```bash
   cd frontend
   npm install
   npm run dev             # http://localhost:8100
   ```

`frontend/src/environments/environment.ts` ya apunta a `apiBaseUrl: 'http://localhost:9090'`,
que coincide con `GATEWAY_PORT=9090` del `.env.example` del backend. Si cambias el
`GATEWAY_PORT`, actualiza también ese archivo (y el `map` de orígenes en
`nginx.conf` si cambias el puerto del frontend).

## Cognito

Registra `http://localhost:8100/auth/callback` como **Allowed callback URL** en el
App Client del User Pool (Hosted UI), tal como indica `frontend/README.md`.

## Notas

- Si despliegas el frontend en otro dominio/puerto, agrega esa URL al `map $http_origin`
  en `backend/nginx/nginx.conf` (regex `"~^https://tu-dominio$"`), o el navegador
  bloqueará las llamadas por CORS aunque el backend responda bien.
- `docker compose logs -f nginx` es útil para depurar CORS/proxy si algo no conecta.
