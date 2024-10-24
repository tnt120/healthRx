import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { DoctorResponse } from '../../models/doctor-response.model';
import { BehaviorSubject, catchError, finalize, Observable, tap, throwError } from 'rxjs';
import { PageResponse } from '../../models/page-response.model';
import { DoctorVerificationRequest } from '../../models/doctor-verification-request.model';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';
import { AdminParameterResponse } from '../../models/admin/admin-parameter-response.model';
import { ParameterRequest } from '../../models/admin/parameter-request.model';
import { Parameter } from '../../models/parameter.model';
import { ErrorCodesService } from '../error-codes/error-codes.service';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly apiUrl = `${environment.apiUrl}/admin`;

  private readonly http = inject(HttpClient);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  private readonly errorCodesService = inject(ErrorCodesService);

  private approvalsSubject = new BehaviorSubject<DoctorResponse[]>([]);

  private loadingSubject = new BehaviorSubject<boolean>(false);

  getLoading(): Observable<boolean> {
    return this.loadingSubject.asObservable();
  }

  getApprovalsSubject() {
    return this.approvalsSubject.asObservable();
  }

  getApprovals(page: number, size: number): Observable<PageResponse<DoctorResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())

    this.loadingSubject.next(true);
    return this.http.get<PageResponse<DoctorResponse>>(`${this.apiUrl}/approvals`, { params }).pipe(
      tap(res => this.approvalsSubject.next(res.content)),
      finalize(() => this.loadingSubject.next(false)),
    );
  }

  verifyDoctor(req: DoctorVerificationRequest): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/verifyDoctor`, req).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie zaweryfikowano lekarza.', type: 'success', duration: 3000 });
        const currApprovals: DoctorResponse[] = this.approvalsSubject.getValue();

        this.approvalsSubject.next(currApprovals.filter(doc => doc.id !== req.doctorId));
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  getParameters(): Observable<AdminParameterResponse> {
    this.loadingSubject.next(true);
    return this.http.get<AdminParameterResponse>(`${this.apiUrl}/parameters`).pipe(
      finalize(() => this.loadingSubject.next(false)),
    );
  }

  addParameter(req: ParameterRequest): Observable<Parameter> {
    return this.http.post<Parameter>(`${this.apiUrl}/parameters`, req).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie dodano parametr.', type: 'success', duration: 3000 });
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  editParameter(req: Partial<ParameterRequest>, id: string): Observable<Parameter> {
    return this.http.patch<Parameter>(`${this.apiUrl}/parameters/${id}`, req).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie edytowano parametr.', type: 'success', duration: 3000 });
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  deleteParameter(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/parameters/${id}`).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie usunięto parametr.', type: 'success', duration: 3000 });
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  handleError(err: any): Observable<never> {
    const message = this.errorCodesService.getErrorMessage(err.error.code);
    this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: message, type: 'error', duration: 3000 });
    return throwError(() => err);
  }
}
