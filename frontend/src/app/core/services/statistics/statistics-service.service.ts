import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { ChartRequest } from '../../models/chart-request.model';
import { BehaviorSubject, finalize, Observable } from 'rxjs';
import { ChartResponse } from '../../models/chart-response.model';
import { StatisticsRequest } from '../../models/statistics-request.model';
import { ParameterStatisticsResponse } from '../../models/parameter-statistics-model';

@Injectable({
  providedIn: 'root'
})
export class StatisticsServiceService {
  private readonly apiUrl = `${environment.apiUrl}/statistics`;

  private readonly http = inject(HttpClient);

  private loadingStatsSubject = new BehaviorSubject<boolean>(false);

  private loadingChartSubject = new BehaviorSubject<boolean>(false);

  getLoadingStatsState() {
    return this.loadingStatsSubject.asObservable();
  }

  setLoadingStatsState(loading: boolean) {
    this.loadingStatsSubject.next(loading);
  }

  getLoadingChartState() {
    return this.loadingChartSubject.asObservable();
  }

  setLoadingChartState(loading: boolean) {
    this.loadingChartSubject.next(loading);
  }

  getChartData(req: ChartRequest): Observable<ChartResponse> {
    this.setLoadingChartState(true);
    return this.http.post<ChartResponse>(`${this.apiUrl}/chart`, req).pipe(
      finalize(() => this.setLoadingChartState(false))
    );
  }

  getParameterStatistics(req: StatisticsRequest): Observable<ParameterStatisticsResponse[]> {
    this.setLoadingStatsState(true);
    return this.http.post<ParameterStatisticsResponse[]>(`${this.apiUrl}/parameters`, req).pipe(
      finalize(() => this.setLoadingStatsState(false))
    );
  }
}
