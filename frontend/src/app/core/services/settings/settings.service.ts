import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { PersonalDataChangeRequest } from '../../models/personal-data-change.model';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  private readonly apiUrl = `${environment.apiUrl}/settings`;

  private readonly http = inject(HttpClient);

  persinalDataChange(data: PersonalDataChangeRequest) {
    return this.http.post<void>(`${this.apiUrl}/personalDataChange`, data);
  }
}
