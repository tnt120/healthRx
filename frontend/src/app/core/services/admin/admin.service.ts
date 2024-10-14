import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { DoctorResponse } from '../../models/doctor-response.model';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { PageResponse } from '../../models/page-response.model';
import { DoctorVerificationRequest } from '../../models/doctor-verification-request.model';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly apiUrl = `${environment.apiUrl}/admin`;

  private readonly http = inject(HttpClient);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  private approvalsSubject = new BehaviorSubject<DoctorResponse[]>([]);

  getApprovalsSubject() {
    return this.approvalsSubject.asObservable();
  }

  getApprovals(page: number, size: number): Observable<PageResponse<DoctorResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())

    return this.http.get<PageResponse<DoctorResponse>>(`${this.apiUrl}/approvals`, { params }).pipe(
      tap(res => this.approvalsSubject.next(res.content))
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
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystąpił błąd podczas weryfikacji lekarza.', type: 'error', duration: 3000 });
        return throwError(() => err);
      })
    );
  }
}
