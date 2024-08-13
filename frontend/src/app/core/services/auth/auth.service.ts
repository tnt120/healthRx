import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { LoginRequest } from '../../models/auth/login-request.model';
import { Observable, switchMap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../user/user.service';
import { Config } from '../../models/user/config.model';
import { RegisterRequest } from '../../models/auth/register-request.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  private readonly http = inject(HttpClient);

  private readonly userService = inject(UserService);

  login(credentials: LoginRequest): Observable<Config> {
    return this.http.post<void>(`${this.apiUrl}/login`, credentials)
      .pipe(
        switchMap(() => this.userService.getInitAndConfig())
      );
  }

  register(credentials: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/register`, credentials);
  }

  refresh(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/refresh`, {});
  }
}
