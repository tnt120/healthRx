import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { UserParameterResponse } from '../../models/user-parameter-response.model';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Store } from '@ngrx/store';
import { Parameter } from '../../models/parameter.model';
import { UserParameterRequest } from '../../models/user-parameter-request.model';

@Injectable({
  providedIn: 'root'
})
export class ParametersService {
  private readonly apiUrl = `${environment.apiUrl}/parameters`;

  private readonly http = inject(HttpClient);

  private readonly store = inject(Store);

  getUserParameters(): Observable<UserParameterResponse[]> {
    return this.http.get<UserParameterResponse[]>(`${this.apiUrl}/users`);
  }

  // editUserParameters(parameters: Parameter[]): Observable<

  setUserParametersMonitor(request: UserParameterRequest[]): Observable<UserParameterResponse[]> {
    return this.http.post<UserParameterResponse[]>(`${this.apiUrl}/monitor`, request);
  }

  editUserParametersMonitor(request: UserParameterRequest[]): Observable<UserParameterResponse[]> {
    return this.http.patch<UserParameterResponse[]>(`${this.apiUrl}/monitor`, request);
  }
}
