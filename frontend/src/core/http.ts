import { environment } from '../environments/environment';
import { authStore } from './auth/authStore';

/**
 * Agrega "Authorization: Bearer {access_token}" a toda request que vaya
 * hacia el gateway del backend (equivalente al Auth Type "Inherit auth
 * from parent" configurado en la colección de Postman, y a
 * auth.interceptor.ts en la versión Angular).
 */
async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);

  if (url.startsWith(environment.apiBaseUrl)) {
    const token = authStore.getAccessToken();
    if (token) headers.set('Authorization', `Bearer ${token}`);
  }

  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }

  const res = await fetch(url, { ...options, headers });

  if (!res.ok) {
    let message = `HTTP ${res.status}`;
    try {
      const body = await res.json();
      message = body?.message ?? message;
    } catch {
      // sin body JSON (ej. 204 No Content en error) - se deja el mensaje genérico
    }
    throw new Error(message);
  }

  if (res.status === 204) {
    return undefined as T;
  }

  return (await res.json()) as T;
}

export const http = {
  get: <T>(url: string) => request<T>(url, { method: 'GET' }),
  post: <T>(url: string, body?: unknown) =>
    request<T>(url, { method: 'POST', body: body !== undefined ? JSON.stringify(body) : undefined }),
  put: <T>(url: string, body?: unknown) =>
    request<T>(url, { method: 'PUT', body: body !== undefined ? JSON.stringify(body) : undefined }),
  patch: <T>(url: string) => request<T>(url, { method: 'PATCH' }),
  delete: <T>(url: string) => request<T>(url, { method: 'DELETE' })
};
