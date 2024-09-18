import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { UserParameterResponse } from '../../models/user-parameter-response.model';
import { BehaviorSubject, catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Parameter } from '../../models/parameter.model';
import { UserParameterRequest } from '../../models/user-parameter-request.model';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';

@Injectable({
  providedIn: 'root'
})
export class ParametersService {
  private readonly apiUrl = `${environment.apiUrl}/parameters`;

  private readonly http = inject(HttpClient);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  getLoadingState() {
    return this.loadingSubject.asObservable();
  }

  setLoadingState(loading: boolean) {
    this.loadingSubject.next(loading);
  }

  getUserParameters(): Observable<UserParameterResponse[]> {
    this.setLoadingState(true);
    return this.http.get<UserParameterResponse[]>(`${this.apiUrl}/users`).pipe(
      finalize(() => this.setLoadingState(false))
    );
  }

  editUserParameters(parameters: Parameter[]): Observable<UserParameterResponse[]> {
    return this.http.patch<UserParameterResponse[]>(`${this.apiUrl}/users`, parameters).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie zaktualizowano parametry', type: 'success', duration: 3000 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Nie udało się zaktualizować parametrów.', type: 'error', duration: 3000 });
        return throwError(() => err)
      })
    );
  }

  setUserParametersMonitor(request: UserParameterRequest[]): Observable<UserParameterResponse[]> {
    return this.http.post<UserParameterResponse[]>(`${this.apiUrl}/monitor`, request);
  }

  editUserParametersMonitor(request: UserParameterRequest): Observable<UserParameterResponse> {
    return this.http.patch<UserParameterResponse>(`${this.apiUrl}/monitor`, request);
  }
}
