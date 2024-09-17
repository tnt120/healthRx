import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { PersonalDataChangeRequest } from '../../models/personal-data-change.model';
import { PasswordChangeRequest } from '../../models/password-change-request.model';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private readonly apiUrl = `${environment.apiUrl}/settings`;

  private readonly http = inject(HttpClient);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  persinalDataChange(data: PersonalDataChangeRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/personalDataChange`, data).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie zaktualizowano profil', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Nie udało się zaktualizować profilu', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }

  passwordChange(request: PasswordChangeRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/passwordChange`, request).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie zaktualizowano hasło', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Nie udało się zaktualizować hasła.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }
}
