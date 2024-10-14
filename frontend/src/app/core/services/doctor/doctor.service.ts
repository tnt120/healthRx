import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SortOption } from '../../models/sort-option.model';
import { DoctorSearch } from '../../models/doctor-search.model';
import { BehaviorSubject, finalize, Observable, tap } from 'rxjs';
import { PageResponse } from '../../models/page-response.model';
import { DoctorResponse } from '../../models/doctor-response.model';
import { ReVerifyDoctorRequest } from '../../models/user/re-verify-doctor-request.model';

@Injectable({
  providedIn: 'root'
})
export class DoctorService {
  private readonly apiUrl = `${environment.apiUrl}/doctor`;

  private readonly http = inject(HttpClient);

  private loadingDoctorsSubject = new BehaviorSubject<boolean>(false);

  private filterChange = new BehaviorSubject<boolean>(false);

  private doctorBehaviorSubject = new BehaviorSubject<DoctorResponse[] | null>(null);

  doctors$ = this.doctorBehaviorSubject.asObservable();

  getFilterChange() {
    return this.filterChange.asObservable();
  }

  emitFilterChange() {
    this.filterChange.next(true);
  }

  getLoadingDoctorsState() {
    return this.loadingDoctorsSubject.asObservable();
  }

  setLoadingDoctorsState(loading: boolean) {
    this.loadingDoctorsSubject.next(loading);
  }

  getDoctors(page: number, size: number, sort: SortOption, doctorSearch: DoctorSearch): Observable<PageResponse<DoctorResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sort.sortBy)
      .set('order', sort.order);

    if (doctorSearch.firstName) params = params.set('firstName', doctorSearch.firstName);
    if (doctorSearch.lastName) params = params.set('lastName', doctorSearch.lastName);
    if (doctorSearch.specialization) params = params.set('specialization', doctorSearch.specialization);
    if (doctorSearch.city) params = params.set('city', doctorSearch.city);

    this.setLoadingDoctorsState(true);
    return this.http.get<PageResponse<DoctorResponse>>(this.apiUrl, { params }).pipe(
      tap((res) => {
        this.doctorBehaviorSubject.next(res.content)
      }),
      finalize(() => this.setLoadingDoctorsState(false))
    );
  }

  reVerify(req: ReVerifyDoctorRequest): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/reVerify`, req);
  }
}
