import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { LoginRequest } from '../../models/auth/login-request.model';
import { Observable, tap } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../user/user.service';
import { RegisterRequest } from '../../models/auth/register-request.model';
import { Store } from '@ngrx/store';
import { configActions } from '../../state/config/config.actions';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  private readonly http = inject(HttpClient);

  private readonly store = inject(Store);

  login(credentials: LoginRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(() => this.store.dispatch(configActions.load())),
      );
  }

  register(credentials: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/register`, credentials);
  }

  refresh(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/refresh`, {});
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, null)
      .pipe(
        tap(() => this.store.dispatch(configActions.logout()))
      );
  }
}
