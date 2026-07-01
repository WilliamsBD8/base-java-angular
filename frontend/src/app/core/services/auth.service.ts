import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api.model';
import { LoginRequest, LoginResponse, RegisterRequest, StoredUser, UserResponse } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly userSignal = signal<StoredUser | null>(this.loadUser());

  readonly currentUser = this.userSignal.asReadonly();

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.baseUrl}/login`, request).pipe(
      tap((response) => this.persistSession(response.data))
    );
  }

  register(request: RegisterRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.baseUrl}/register`, request).pipe(
      tap((response) => this.persistSession(response.data))
    );
  }

  me(): Observable<ApiResponse<UserResponse>> {
    return this.http.get<ApiResponse<UserResponse>>(`${this.baseUrl}/me`);
  }

  logout(): Observable<ApiResponse<void>> {
    return this.http.post<ApiResponse<void>>(`${this.baseUrl}/logout`, {}).pipe(
      tap(() => this.clearSession())
    );
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token');
  }

  getRoles(): string[] {
    return this.userSignal()?.roles ?? [];
  }

  private persistSession(data: LoginResponse): void {
    localStorage.setItem('token', data.token);
    const user: StoredUser = {
      userId: data.userId,
      name: data.name,
      email: data.email,
      roles: data.roles
    };
    localStorage.setItem('user', JSON.stringify(user));
    this.userSignal.set(user);
  }

  private clearSession(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.userSignal.set(null);
    this.router.navigate(['/login']);
  }

  private loadUser(): StoredUser | null {
    const raw = localStorage.getItem('user');
    if (!raw) return null;
    try {
      return JSON.parse(raw) as StoredUser;
    } catch {
      return null;
    }
  }
}
