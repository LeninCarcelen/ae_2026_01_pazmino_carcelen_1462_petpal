import { http } from '../http';
import { environment } from '../../environments/environment';
import { UserRequest, UserResponse } from '../models/user.model';

const BASE = `${environment.apiBaseUrl}/users/api/users`;

export const userService = {
  /** POST está abierto a los 4 roles: cualquier usuario autenticado crea su propio perfil tras el primer login. */
  createProfile: (payload: UserRequest) => http.post<UserResponse>(BASE, payload),
  me: () => http.get<UserResponse>(`${BASE}/me`),
  /** Solo ADMIN. */
  list: () => http.get<UserResponse[]>(BASE),
  getById: (id: number) => http.get<UserResponse>(`${BASE}/${id}`),
  update: (id: number, payload: UserRequest) => http.put<UserResponse>(`${BASE}/${id}`, payload),
  remove: (id: number) => http.delete<void>(`${BASE}/${id}`)
};
