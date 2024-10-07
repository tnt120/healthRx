import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient } from '@angular/common/http';
import { ChartRequest } from '../../models/chart-request.model';
import { BehaviorSubject, finalize, Observable, map } from 'rxjs';
import { ChartResponse } from '../../models/chart-response.model';
import { StatisticsRequest } from '../../models/statistics-request.model';
import { ParameterStatisticsResponse } from '../../models/parameter-statistics-model';
import { DrugStatisticsResponse } from '../../models/drug-statistics-model';
import { StatisticsType } from '../../enums/statistics-type.enum';
import { Statistics_Type_Init } from '../../constants/statistics-type-init';

@Injectable({
  providedIn: 'root'
})
export class StatisticsServiceService {
  private readonly apiUrl = `${environment.apiUrl}/statistics`;

  private readonly http = inject(HttpClient);

  private loadingStatsSubject = new BehaviorSubject<Map<StatisticsType, boolean>>(Statistics_Type_Init);

  private loadingChartSubject = new BehaviorSubject<Map<StatisticsType, boolean>>(Statistics_Type_Init);

  getLoadingStatsState(type: StatisticsType): Observable<boolean> {
    return this.loadingStatsSubject.asObservable().pipe(
      map(stateMap => stateMap.get(type) || false)
    );
  }

  setLoadingStatsState(type: StatisticsType, loading: boolean) {
    const currState = this.loadingStatsSubject.getValue();
    const updatedState = new Map(currState).set(type, loading);

    this.loadingStatsSubject.next(updatedState);
  }

  getLoadingChartState(type: StatisticsType): Observable<boolean> {
    return this.loadingChartSubject.asObservable().pipe(
      map(stateMap => stateMap.get(type) || false)
    );
  }

  setLoadingChartState(type: StatisticsType, loading: boolean) {
    const currState = this.loadingChartSubject.getValue();
    const updatedState = new Map(currState).set(type, loading);

    this.loadingChartSubject.next(updatedState);
  }

  getChartData(req: ChartRequest): Observable<ChartResponse> {
    const type: StatisticsType = StatisticsType[req.type as keyof typeof StatisticsType];
    console.log(type, req.type);

    this.setLoadingChartState(type, true);

    return this.http.post<ChartResponse>(`${this.apiUrl}/chart`, req).pipe(
      finalize(() => this.setLoadingChartState(type, false))
    );
  }

  getParameterStatistics(req: StatisticsRequest): Observable<ParameterStatisticsResponse[]> {
    this.setLoadingStatsState(StatisticsType.PARAMETER, true);
    return this.http.post<ParameterStatisticsResponse[]>(`${this.apiUrl}/parameters`, req).pipe(
      finalize(() => this.setLoadingStatsState(StatisticsType.PARAMETER, false))
    );
  }

  getDrugsStatistics(req: StatisticsRequest): Observable<DrugStatisticsResponse[]> {
    this.setLoadingStatsState(StatisticsType.DRUG, true);
    return this.http.post<DrugStatisticsResponse[]>(`${this.apiUrl}/drugs`, req).pipe(
      finalize(() => this.setLoadingStatsState(StatisticsType.DRUG, false))
    );
  }
}
