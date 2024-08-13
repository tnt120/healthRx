import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, retry, switchMap, tap, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req)
    .pipe(
      retry(1),
      catchError((err: HttpErrorResponse) => {
        const errorBody = err.error;

        if (errorBody && errorBody.message === 'Access token expired') {
          return authService.refresh().pipe(
            tap(() => console.log('Token refreshed')),
            switchMap(() => {
              return next(req.clone());
            }),
            catchError((err) => {
              router.navigate(['/login']);

              // tutaj notifikacja o błędzie

              return throwError(() => err);
            })
          );
        }
        return throwError(() => err);
      })
    );
};
