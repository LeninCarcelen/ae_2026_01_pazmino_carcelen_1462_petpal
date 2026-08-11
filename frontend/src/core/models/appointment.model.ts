import { VeterinarianResponse } from './veterinarian.model';

// Coincide con AppointmentDto.kt
export interface AppointmentRequest {
  date: string; // ISO LocalDateTime, ej. 2026-09-01T10:00:00
  reason: string;
  petId: number;
  veterinarianIds: number[];
}

export interface AppointmentResponse {
  id: number;
  date: string;
  reason: string;
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | string;
  petId: number;
  petName: string;
  veterinarians: VeterinarianResponse[];
}
