import { jwtDecode } from 'jwt-decode';
import { environment } from '../../environments/environment';
import { Role } from '../models/role.model';

interface TokenResponse {
  access_token: string;
  id_token: string;
  refresh_token?: string;
  expires_in: number;
  token_type: string;
}

interface DecodedAccessToken {
  sub: string;
  'cognito:groups'?: string[];
  exp: number;
  [key: string]: unknown;
}

const STORAGE_KEY = 'petpal_auth';

type Listener = () => void;

/**
 * Maneja el flujo OAuth2 Authorization Code contra el Hosted UI de Cognito,
 * el mismo que se valida manualmente en Postman (Auth > Exchange code for token).
 *
 * Roles: se leen del claim `cognito:groups` del access_token, igual que hace
 * CognitoJwtAuthenticationConverter.kt en ambos microservicios.
 *
 * Es un singleton plano (equivalente al servicio @Injectable de Angular,
 * que también era un singleton a nivel de aplicación) con un pub-sub simple
 * para que los componentes React puedan re-renderizar cuando cambia el token.
 */
class AuthStore {
  private accessToken: string | null = this.readStoredToken();
  private listeners = new Set<Listener>();

  subscribe(listener: Listener): () => void {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  }

  private notify(): void {
    this.listeners.forEach((l) => l());
  }

  /** Redirige al Hosted UI de Cognito para iniciar sesión. */
  login(): void {
    const params = new URLSearchParams({
      client_id: environment.cognito.clientId,
      response_type: 'code',
      redirect_uri: environment.cognito.redirectUri,
      scope: environment.cognito.scope
    });
    window.location.href = `${environment.cognito.domain}/oauth2/authorize?${params.toString()}`;
  }

  /** Se llama desde la página de callback (/auth/callback?code=...). */
  async handleCallback(code: string): Promise<void> {
    const body = new URLSearchParams({
      grant_type: 'authorization_code',
      client_id: environment.cognito.clientId,
      code,
      redirect_uri: environment.cognito.redirectUri
    });

    const res = await fetch(`${environment.cognito.domain}/oauth2/token`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: body.toString()
    });

    if (!res.ok) {
      throw new Error(`Token exchange failed: ${res.status}`);
    }

    const response = (await res.json()) as TokenResponse;
    localStorage.setItem(STORAGE_KEY, JSON.stringify(response));
    this.accessToken = response.access_token;
    this.notify();
  }

  logout(): void {
    localStorage.removeItem(STORAGE_KEY);
    this.accessToken = null;
    this.notify();
    const params = new URLSearchParams({
      client_id: environment.cognito.clientId,
      logout_uri: environment.cognito.redirectUri.replace('/auth/callback', '/login')
    });
    window.location.href = `${environment.cognito.domain}/logout?${params.toString()}`;
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  isAuthenticated(): boolean {
    const token = this.getAccessToken();
    if (!token) return false;
    try {
      const decoded = jwtDecode<DecodedAccessToken>(token);
      return decoded.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }

  /** Roles del usuario actual, tal como los expone cognito:groups. */
  getRoles(): Role[] {
    const token = this.getAccessToken();
    if (!token) return [];
    try {
      const decoded = jwtDecode<DecodedAccessToken>(token);
      return (decoded['cognito:groups'] ?? []).filter((g): g is Role =>
        Object.values(Role).includes(g as Role)
      );
    } catch {
      return [];
    }
  }

  hasRole(...roles: Role[]): boolean {
    const mine = this.getRoles();
    return roles.some((r) => mine.includes(r));
  }

  isAdmin(): boolean {
    return this.hasRole(Role.ADMIN);
  }

  /** 'sub' de Cognito - identifica al usuario ante el microservicio users. */
  getCognitoSub(): string | null {
    const token = this.getAccessToken();
    if (!token) return null;
    try {
      return jwtDecode<DecodedAccessToken>(token).sub;
    } catch {
      return null;
    }
  }

  private readStoredToken(): string | null {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    try {
      return (JSON.parse(raw) as TokenResponse).access_token;
    } catch {
      return null;
    }
  }
}

export const authStore = new AuthStore();
