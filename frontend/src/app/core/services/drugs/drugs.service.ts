import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { SortOption } from '../../models/sort-option.model';
import { PageResponse } from '../../models/page-response.model';
import { DrugResponse } from '../../models/drug-response.model';
import { UserDrugsResponse } from '../../models/user-drugs-response.model';
import { UserDrugMonitorResponse } from '../../models/user-drug-monitor-response.model';

@Injectable({
  providedIn: 'root'
})
export class DrugsService {
  private readonly apiUrl = `${environment.apiUrl}/drugs`;

  private readonly http = inject(HttpClient);

  private loadingDrugsSubject = new BehaviorSubject<boolean>(false);

  private loadingMonitorSubject = new BehaviorSubject<boolean>(false);

  private filterChange = new BehaviorSubject<boolean>(false);

  getLoadingDrugsState() {
    return this.loadingDrugsSubject.asObservable();
  }

  setLoadingDrugsState(loading: boolean) {
    this.loadingDrugsSubject.next(loading);
  }

  getLoadingMonitorState() {
    return this.loadingMonitorSubject.asObservable();
  }

  setLoadingMonitorState(loading: boolean) {
    this.loadingMonitorSubject.next(loading);
  }

  getFilterChange() {
    return this.filterChange.asObservable();
  }

  emitFilterChange() {
    this.filterChange.next(true);
  }

  getDrugs(page: number, size: number, sort: SortOption, searchName: string | null): Observable<PageResponse<DrugResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sort.sortBy)
      .set('order', sort.order);

    if (searchName) params = params.set('name', searchName);

    return this.http.get<PageResponse<DrugResponse>>(`${this.apiUrl}`, { params });
  }

  getUserDrugs(page: number, size: number, sort: SortOption): Observable<PageResponse<UserDrugsResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sort.sortBy)
      .set('order', sort.order);

    this.setLoadingDrugsState(true);
    return this.http.get<PageResponse<UserDrugsResponse>>(`${this.apiUrl}/user`, { params }).pipe(
      tap(() => {
        this.setLoadingDrugsState(false);
      })
    );
  }

  getUserDrugMonitor(): Observable<UserDrugMonitorResponse[]> {
    this.setLoadingMonitorState(true);
    return this.http.get<UserDrugMonitorResponse[]>(`${this.apiUrl}/monitor`).pipe(
      tap(() => {
        this.setLoadingMonitorState(false);
      })
    );
  }
}
