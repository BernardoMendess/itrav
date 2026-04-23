import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { map, Observable, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse, AuthPayload, AuthRequest, RegisterRequest, User } from './models';

const TOKEN_KEY = 'itrav.token';
const USER_KEY = 'itrav.user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = environment.apiUrl;

  private readonly tokenState = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  private readonly userState = signal<User | null>(this.readUserFromStorage());

  readonly token = computed(() => this.tokenState());
  readonly user = computed(() => this.userState());
  readonly isAuthenticated = computed(() => !!this.tokenState());

  constructor(private readonly http: HttpClient) {}

  register(payload: RegisterRequest): Observable<AuthPayload> {
    return this.http
      .post<ApiResponse<AuthPayload>>(`${this.apiUrl}/auth/register`, payload)
      .pipe(
        map((response) => response.data),
        tap((data) => this.persistSession(data))
      );
  }

  login(payload: AuthRequest): Observable<AuthPayload> {
    return this.http
      .post<ApiResponse<AuthPayload>>(`${this.apiUrl}/auth/login`, payload)
      .pipe(
        map((response) => response.data),
        tap((data) => this.persistSession(data))
      );
  }

  loadCurrentUser(): Observable<User> {
    const email = this.getEmailFromToken();
    if (!email) {
      throw new Error('Token inválido');
    }

    const params = new HttpParams().set('email', email);
    return this.http
      .get<ApiResponse<User>>(`${this.apiUrl}/users/me`, { params })
      .pipe(
        map((response) => response.data),
        tap((user) => {
          this.userState.set(user);
          localStorage.setItem(USER_KEY, JSON.stringify(user));
        })
      );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.tokenState.set(null);
    this.userState.set(null);
  }

  getToken(): string | null {
    return this.tokenState();
  }

  private persistSession(payload: AuthPayload): void {
    this.tokenState.set(payload.token);
    this.userState.set(payload.user);
    localStorage.setItem(TOKEN_KEY, payload.token);
    localStorage.setItem(USER_KEY, JSON.stringify(payload.user));
  }

  private readUserFromStorage(): User | null {
    const raw = localStorage.getItem(USER_KEY);
    if (!raw) {
      return null;
    }

    try {
      return JSON.parse(raw) as User;
    } catch {
      return null;
    }
  }

  private getEmailFromToken(): string | null {
    const token = this.tokenState();
    if (!token) {
      return null;
    }

    const chunks = token.split('.');
    if (chunks.length !== 3) {
      return null;
    }

    try {
      const payload = JSON.parse(atob(chunks[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload.sub ?? null;
    } catch {
      return null;
    }
  }
}
