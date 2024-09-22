import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { InvitationRequest } from '../../models/invitation-request.model';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';
import { InvitationResponse } from '../../models/invitation-response.model';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {
  private readonly apiUrl = `${environment.apiUrl}/friendship`;

  private readonly http = inject(HttpClient);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  sendInvitation(request: InvitationRequest): Observable<InvitationResponse> {
    return this.http.post<InvitationResponse>(`${this.apiUrl}/invite`, request).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie wysłano zaproszenie.', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystawił błąd podczas wysyłania zaproszenia.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }

  acceptInvitation(request: InvitationRequest): Observable<InvitationResponse> {
    return this.http.post<InvitationResponse>(`${this.apiUrl}/accept`, request).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Dodano użytkownika.', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Błąd podczas akcpetacji zaproszenia.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }

  rejectInvitation(request: InvitationRequest): Observable<InvitationResponse> {
    return this.http.post<InvitationResponse>(`${this.apiUrl}/reject`, request).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Odrzucono zaproszenie.', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Błąd podczas odrzucenia zaproszenia.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }

  resendInvitation(request: InvitationRequest): Observable<InvitationResponse> {
    return this.http.post<InvitationResponse>(`${this.apiUrl}/resend`, request).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Ponowne zaprosznie zostało pomyślnie wysłane.', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystawił błąd podczas wysyłania zaproszenia.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }

  cancelInvitation(id: string): Observable<InvitationResponse> {
    return this.http.delete<InvitationResponse>(`${this.apiUrl}/remove/${id}`).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Usnięto użykotnika ze znajomych.', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Błąd podczas usuwania ze znajomych.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }
}
