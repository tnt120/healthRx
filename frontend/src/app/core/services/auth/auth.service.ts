import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { LoginRequest } from '../../models/auth/login-request.model';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
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

  private refreshInProgress$ = new BehaviorSubject<boolean>(false);

  get isRefreshInProgress(): Observable<boolean> {
    return this.refreshInProgress$.asObservable();
  }

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
    this.refreshInProgress$.next(true);
    return this.http.post<void>(`${this.apiUrl}/refresh`, {}).pipe(
      tap(() => {
        this.refreshInProgress$.next(false);
      }),
      catchError((err) => {
        this.refreshInProgress$.next(false);
        return throwError(() => err);
      })
    );
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, null)
      .pipe(
        tap(() => this.store.dispatch(configActions.logout()))
      );
  }
}
