// Coincide con OwnerDto.kt
export interface OwnerRequest {
  name: string;
  email: string;
  phone: string;
}

export interface OwnerResponse {
  id: number;
  name: string;
  email: string;
  phone: string;
}
