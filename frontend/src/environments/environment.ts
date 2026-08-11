// Equivalente a environment.ts / environment.prod.ts de la versión Angular.
// Vite resuelve el modo automáticamente (import.meta.env.PROD) al hacer
// `vite build` (producción) vs `vite dev` (desarrollo).

interface CognitoConfig {
  domain: string;
  clientId: string;
  redirectUri: string;
  scope: string;
  region: string;
  userPoolId: string;
}

interface Environment {
  production: boolean;
  apiBaseUrl: string;
  cognito: CognitoConfig;
}

const dev: Environment = {
  production: false,

  // Gateway Nginx (ver docker-compose.yml / .env.example -> GATEWAY_PORT=9090)
  apiBaseUrl: 'http://localhost:9090',

  cognito: {
    // Hosted UI domain del User Pool (ver .env -> COGNITO_ISSUER_URI / consola de Cognito)
    domain: 'https://us-east-1yhlwgvu4h.auth.us-east-1.amazoncognito.com',
    clientId: '2qp1i6cg0elte4o31nddkud9s2',
    // Debe estar registrado como Allowed Callback URL en el App Client de Cognito
    redirectUri: 'http://localhost:8100/auth/callback',
    scope: 'openid email',
    region: 'us-east-1',
    userPoolId: 'us-east-1_YHlWgVU4H'
  }
};

const prod: Environment = {
  production: true,
  apiBaseUrl: 'https://api.petpal.example.com',
  cognito: {
    domain: 'https://us-east-1yhlwgvu4h.auth.us-east-1.amazoncognito.com',
    clientId: '2qp1i6cg0elte4o31nddkud9s2',
    redirectUri: 'https://app.petpal.example.com/auth/callback',
    scope: 'openid email',
    region: 'us-east-1',
    userPoolId: 'us-east-1_YHlWgVU4H'
  }
};

export const environment: Environment = import.meta.env.PROD ? prod : dev;
