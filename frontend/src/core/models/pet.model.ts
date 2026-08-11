// Coincide con PetDto.kt
export interface PetRequest {
  name: string;
  species: string;
  breed: string;
  birthDate: string | null; // ISO yyyy-MM-dd
  ownerId: number;
}

export interface PetResponse {
  id: number;
  name: string;
  species: string;
  breed: string;
  birthDate: string | null;
  ownerId: number;
  ownerName: string;
}
