// Coincide con AppointmentDto.kt (VeterinarianRequest / VeterinarianResponse)
export interface VeterinarianRequest {
  name: string;
  specialty: string;
}

export interface VeterinarianResponse {
  id: number;
  name: string;
  specialty: string;
}
