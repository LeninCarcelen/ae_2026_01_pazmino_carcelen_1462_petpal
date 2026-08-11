import { http } from '../http';
import { environment } from '../../environments/environment';
import { PetRequest, PetResponse } from '../models/pet.model';

const BASE = `${environment.apiBaseUrl}/petpal/api/pets`;

export const petService = {
  list: (ownerId?: number) => http.get<PetResponse[]>(ownerId ? `${BASE}?ownerId=${ownerId}` : BASE),
  getById: (id: number) => http.get<PetResponse>(`${BASE}/${id}`),
  create: (payload: PetRequest) => http.post<PetResponse>(BASE, payload),
  update: (id: number, payload: PetRequest) => http.put<PetResponse>(`${BASE}/${id}`, payload),
  remove: (id: number) => http.delete<void>(`${BASE}/${id}`)
};
