import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Config } from '../../models/user/config.model';
import { VerificationDataResponse } from '../../models/user/verification-data-response.model';
import { UserVerificationRequest } from '../../models/user/user-verification-request.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/user`;

  private readonly http = inject(HttpClient);

  getInitAndConfig(): Observable<Config> {
    return this.http.get<Config>(`${this.apiUrl}/initAndConfig`);
  }

  getVerificationData(verificationToken: string): Observable<VerificationDataResponse> {
    return this.http.post<VerificationDataResponse>(`${this.apiUrl}/getVerificationData`, { verificationToken });
  }

  verifyUser(request: UserVerificationRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/verification`, request);
  }
}
