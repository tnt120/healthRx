import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, EMPTY, retry, switchMap, tap, throwError } from 'rxjs';
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
            tap(() => console.log('Token refreshed')),
            switchMap(() => {
              return next(req.clone());
            }),
            catchError((err) => {
              return authService.logout().pipe(
                tap(() => {
                  customSnackbarService.openCustomSnackbar({ title: 'Ostrzeżenie', message: 'Wystąpił problem z sesją i zostałeś wylogowany. Spróbuj zalogować się ponownie.', type: 'warning', duration: 5000 });
                  router.navigate(['login']);
                }),
                switchMap(() => EMPTY)
              );
            })
          );
        }
        return throwError(() => err);
      })
    );
};
