// Coincide con UserDto.kt (microservicio users)
export interface UserRequest {
  cognitoSub: string;
  email: string;
  fullName: string;
  phone: string;
  role: string;
}

export interface UserResponse {
  id: number;
  cognitoSub: string;
  email: string;
  fullName: string;
  phone: string;
  role: string;
  createdAt: string;
}
