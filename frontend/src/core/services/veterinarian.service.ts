import { http } from '../http';
import { environment } from '../../environments/environment';
import { VeterinarianRequest, VeterinarianResponse } from '../models/veterinarian.model';

const BASE = `${environment.apiBaseUrl}/petpal/api/veterinarians`;

export const veterinarianService = {
  list: () => http.get<VeterinarianResponse[]>(BASE),
  getById: (id: number) => http.get<VeterinarianResponse>(`${BASE}/${id}`),
  create: (payload: VeterinarianRequest) => http.post<VeterinarianResponse>(BASE, payload),
  // DELETE es exclusivo de ADMIN en el backend (ver carpeta Negativos en Postman)
  remove: (id: number) => http.delete<void>(`${BASE}/${id}`)
};
