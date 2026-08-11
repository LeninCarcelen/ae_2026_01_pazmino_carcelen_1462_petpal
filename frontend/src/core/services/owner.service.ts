import { http } from '../http';
import { environment } from '../../environments/environment';
import { OwnerRequest, OwnerResponse } from '../models/owner.model';

const BASE = `${environment.apiBaseUrl}/petpal/api/owners`;

// Roles habilitados por método, según SecurityConfig.kt:
// GET -> ADMIN, VET, HAIRDRESSER, OWNER | POST -> ADMIN, VET, HAIRDRESSER | DELETE -> ADMIN
export const ownerService = {
  list: () => http.get<OwnerResponse[]>(BASE),
  getById: (id: number) => http.get<OwnerResponse>(`${BASE}/${id}`),
  create: (payload: OwnerRequest) => http.post<OwnerResponse>(BASE, payload),
  remove: (id: number) => http.delete<void>(`${BASE}/${id}`)
};
