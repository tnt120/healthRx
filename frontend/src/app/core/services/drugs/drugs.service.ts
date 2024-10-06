import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject, finalize, Observable, tap } from 'rxjs';
import { SortOption } from '../../models/sort-option.model';
import { PageResponse } from '../../models/page-response.model';
import { DrugResponse } from '../../models/drug-response.model';
import { UserDrugsResponse } from '../../models/user-drugs-response.model';
import { UserDrugMonitorResponse } from '../../models/user-drug-monitor-response.model';
import { UserDrugMonitorRequest } from '../../models/user-drug-monitor-request.model';
import { UserDrugsRequest } from '../../models/user-drugs-request.mode';
import { DrugPacksResponse } from '../../models/drug-packs-response';

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

    this.setLoadingDrugsState(true);
    return this.http.get<PageResponse<DrugResponse>>(`${this.apiUrl}`, { params }).pipe(
      finalize(() => {
        this.setLoadingDrugsState(false);
      })
    );
  }

  getDrugsUser(): Observable<DrugResponse[]> {
    return this.http.get<DrugResponse[]>(`${this.apiUrl}/drugsUser`);
  }

  getDrugPacks(drugId: number): Observable<DrugPacksResponse> {
    return this.http.get<DrugPacksResponse>(`${this.apiUrl}/packs/${drugId}`);
  }

  getUserDrugs(page: number, size: number, sort: SortOption): Observable<PageResponse<UserDrugsResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sort.sortBy)
      .set('order', sort.order);

    this.setLoadingDrugsState(true);
    return this.http.get<PageResponse<UserDrugsResponse>>(`${this.apiUrl}/user`, { params }).pipe(
      finalize(() => {
        this.setLoadingDrugsState(false);
      })
    );
  }

  addUserDrug(request: UserDrugsRequest): Observable<UserDrugsResponse> {
    return this.http.post<UserDrugsResponse>(`${this.apiUrl}/user`, request);
  }

  updateUserDrug(request: UserDrugsRequest, id: string): Observable<UserDrugsResponse> {
    return this.http.put<UserDrugsResponse>(`${this.apiUrl}/user/${id}`, request);
  }

  deleteUserDrug(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/user/${id}`);
  }

  getUserDrugMonitor(): Observable<UserDrugMonitorResponse[]> {
    this.setLoadingMonitorState(true);
    return this.http.get<UserDrugMonitorResponse[]>(`${this.apiUrl}/monitor`).pipe(
      tap(() => {
        this.setLoadingMonitorState(false);
      })
    );
  }

  setMonitorDrug(request: UserDrugMonitorRequest): Observable<UserDrugMonitorResponse> {
    return this.http.post<UserDrugMonitorResponse>(`${this.apiUrl}/monitor`, request);
  }

  editMonitorDrug(request: UserDrugMonitorRequest): Observable<UserDrugMonitorResponse> {
    return this.http.patch<UserDrugMonitorResponse>(`${this.apiUrl}/monitor`, request);
  }

  deleteMonitorDrug(drugId: number, time: string): Observable<void> {
    let params = new HttpParams()
      .set('drugId', drugId)
      .set('time', time);

    return this.http.delete<void>(`${this.apiUrl}/monitor`, { params });
  }
}
