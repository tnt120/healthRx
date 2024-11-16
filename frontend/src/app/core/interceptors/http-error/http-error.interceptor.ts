import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, delay, EMPTY, Observable, retry, switchMap, tap, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { AuthService } from '../../services/auth/auth.service';
import { Router } from '@angular/router';
import { CustomSnackbarService } from '../../services/custom-snackbar/custom-snackbar.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const customSnackbarService = inject(CustomSnackbarService);

  return next(req)
    .pipe(
      retry(1),
      catchError((err: HttpErrorResponse) => {
        const errorBody = err.error;

        if (errorBody && errorBody.message === 'Access token expired') {
          return authService.refresh().pipe(
            switchMap(() => {
              return next(req.clone());
            }),
            catchError((err) => {
              return handleAuthError(err, authService, router, customSnackbarService);
            })
          );
        }
        return throwError(() => err);
      })
    );
};

function handleAuthError(err: any, authService: AuthService, router: Router, customSnackbarService: CustomSnackbarService): Observable<never> {
  return authService.logout().pipe(
    tap(() => {
      customSnackbarService.openCustomSnackbar(
        { title: 'Ostrzeżenie', message: 'Wystąpił problem z sesją i zostaniesz ponownie wylogowany.', type: 'warning', duration: 3000 }
      );
    }),
    delay(3000),
    tap(() => router.navigate(['login'])),
    switchMap(() => EMPTY)
  );
}
