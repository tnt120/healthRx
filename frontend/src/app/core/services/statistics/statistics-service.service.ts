import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { ChartRequest } from '../../models/chart-request.model';
import { Observable } from 'rxjs';
import { ChartResponse } from '../../models/chart-response.model';

@Injectable({
  providedIn: 'root'
})
export class StatisticsServiceService {
  private readonly apiUrl = `${environment.apiUrl}/statistics`;

  private readonly http = inject(HttpClient);

  getChartData(req: ChartRequest): Observable<ChartResponse> {
    return this.http.post<ChartResponse>(`${this.apiUrl}/chart`, req);
  }
}
