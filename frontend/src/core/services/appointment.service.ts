import { http } from '../http';
import { environment } from '../../environments/environment';
import { AppointmentRequest, AppointmentResponse } from '../models/appointment.model';

const BASE = `${environment.apiBaseUrl}/petpal/api/appointments`;

export const appointmentService = {
  list: (petId?: number) => http.get<AppointmentResponse[]>(petId ? `${BASE}?petId=${petId}` : BASE),
  getById: (id: number) => http.get<AppointmentResponse>(`${BASE}/${id}`),
  create: (payload: AppointmentRequest) => http.post<AppointmentResponse>(BASE, payload),
  updateStatus: (id: number, status: string) =>
    http.patch<AppointmentResponse>(`${BASE}/${id}/status?status=${encodeURIComponent(status)}`),
  remove: (id: number) => http.delete<void>(`${BASE}/${id}`)
};
