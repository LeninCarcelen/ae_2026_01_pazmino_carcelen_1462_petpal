import { http } from '../http';
import { environment } from '../../environments/environment';
import { VaccineRequest, VaccineResponse } from '../models/vaccine.model';

const BASE = `${environment.apiBaseUrl}/petpal/api/vaccines`;

export const vaccineService = {
  list: (petId?: number) => http.get<VaccineResponse[]>(petId ? `${BASE}?petId=${petId}` : BASE),
  getById: (id: number) => http.get<VaccineResponse>(`${BASE}/${id}`),
  create: (payload: VaccineRequest) => http.post<VaccineResponse>(BASE, payload),
  remove: (id: number) => http.delete<void>(`${BASE}/${id}`)
};
