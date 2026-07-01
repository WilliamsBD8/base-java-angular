export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  role: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  userId: number;
  name: string;
  email: string;
  roles: string[];
}

export interface UserResponse {
  userId: number;
  name: string;
  email: string;
  roles: string[];
  state: string;
}

export interface StoredUser {
  userId: number;
  name: string;
  email: string;
  roles: string[];
}
