import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Config } from '../../models/user/config.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly apiUrl = `${environment.apiUrl}/user`;

  private readonly http = inject(HttpClient);

  getInitAndConfig(): Observable<Config> {
    return this.http.get<Config>(`${this.apiUrl}/initAndConfig`);
  }
}
