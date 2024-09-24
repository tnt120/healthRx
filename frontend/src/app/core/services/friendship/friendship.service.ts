import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { InvitationRequest } from '../../models/invitation-request.model';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';
import { InvitationResponse } from '../../models/invitation-response.model';
import { FriendshipPermissions, FriendshipResponse } from '../../models/friendship-response.model';
import { PageResponse } from '../../models/page-response.model';
import { FriendshipSearch } from '../../models/friendship-search.model';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {
  private readonly apiUrl = `${environment.apiUrl}/friendship`;

  private readonly http = inject(HttpClient);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  private friendshipsSubject = new BehaviorSubject<FriendshipResponse[]>([]);

  friendships$ = this.friendshipsSubject.asObservable();

  private loadingFriendshipsSubject = new BehaviorSubject<boolean>(false);
  private loadingFriendshipsPendingSubject = new BehaviorSubject<boolean>(false);
  private loadingFriendshipsRejectedSubject = new BehaviorSubject<boolean>(false);

  loadingFriendships$ = this.loadingFriendshipsSubject.asObservable();
  loadingFriendshipsPending$ = this.loadingFriendshipsPendingSubject.asObservable();
  loadingFriendshipsRejected$ = this.loadingFriendshipsRejectedSubject.asObservable();

  private filterChange = new BehaviorSubject<boolean>(false);

  getFilterChange() {
    return this.filterChange.asObservable();
  }

  emitFilterChange() {
    this.filterChange.next(true);
  }

  setLoadingFriendshipsState(loading: boolean) {
    this.loadingFriendshipsSubject.next(loading);
  }

  setLoadingFriendshipsPendingState(loading: boolean) {
    this.loadingFriendshipsPendingSubject.next(loading);
  }

  setLoadingFriendshipsRejectedState(loading: boolean) {
    this.loadingFriendshipsRejectedSubject.next(loading);
  }

  getFriendships(page: number, size: number, friendshipSearch: FriendshipSearch): Observable<PageResponse<FriendshipResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', 'id')
      .set('order', 'asc');

    if (friendshipSearch.firstName) params = params.set('firstName', friendshipSearch.firstName);
    if (friendshipSearch.lastName) params = params.set('lastName', friendshipSearch.lastName);

    this.setLoadingFriendshipsState(true);
    return this.http.get<PageResponse<FriendshipResponse>>(`${this.apiUrl}/accepted`, { params }).pipe(
      tap(res => {
        this.friendshipsSubject.next(res.content);
      }),
      finalize(() => this.setLoadingFriendshipsState(false))
    );
  }

  updateFriendships(friendshipId: string, action: 'delete' | 'permissionUpdate', newPermissions: FriendshipPermissions | null = null) {
    const currentFrienships = this.friendshipsSubject.getValue();

    switch (action) {
      case 'delete': {
        const updatedFriendships = currentFrienships.filter(f => f.friendshipId !== friendshipId);
        this.friendshipsSubject.next(updatedFriendships);
        break;
      }
      case 'permissionUpdate': {
        if (newPermissions) {
          const updatedFriendships = currentFrienships.map(f => {
            if (f.friendshipId === friendshipId) {
              return { ...f, permissions: newPermissions };
            }

            return f;
          })

          this.friendshipsSubject.next(updatedFriendships);
        }
      }
    }
  }

  getFriendshipsPending(): Observable<FriendshipResponse[]> {
    this.setLoadingFriendshipsPendingState(true);
    return this.http.get<FriendshipResponse[]>(`${this.apiUrl}/pending`).pipe(
      finalize(() => this.setLoadingFriendshipsPendingState(false))
    );
  }

  getFriendshipsRejected(): Observable<FriendshipResponse[]> {
    this.setLoadingFriendshipsRejectedState(true);
    return this.http.get<FriendshipResponse[]>(`${this.apiUrl}/rejected`).pipe(
      finalize(() => this.setLoadingFriendshipsRejectedState(false))
    );
  }

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

  cancelInvitation(id: string, isAccepted: boolean = true): Observable<InvitationResponse> {
    return this.http.delete<InvitationResponse>(`${this.apiUrl}/remove/${id}`).pipe(
      tap(() => {
        const successMessage = isAccepted ? 'Usunięto użytkownika ze znajomych.' : 'Anulowano zaproszenie.';
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: successMessage, type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Błąd podczas usuwania ze znajomych.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }
}
