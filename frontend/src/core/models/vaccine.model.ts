// Coincide con VaccineDto.kt
export interface VaccineRequest {
  name: string;
  dateApplied: string; // ISO yyyy-MM-dd
  nextDueDate: string | null;
  petId: number;
}

export interface VaccineResponse {
  id: number;
  name: string;
  dateApplied: string;
  nextDueDate: string | null;
  petId: number;
  petName: string;
}
