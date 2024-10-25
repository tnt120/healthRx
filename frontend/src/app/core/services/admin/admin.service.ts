import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { DoctorResponse } from '../../models/doctor-response.model';
import { BehaviorSubject, catchError, finalize, Observable, Subject, tap, throwError } from 'rxjs';
import { PageResponse } from '../../models/page-response.model';
import { DoctorVerificationRequest } from '../../models/doctor-verification-request.model';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';
import { AdminParameterResponse } from '../../models/admin/admin-parameter-response.model';
import { ParameterRequest } from '../../models/admin/parameter-request.model';
import { Parameter } from '../../models/parameter.model';
import { ErrorCodesService } from '../error-codes/error-codes.service';
import { Activity } from '../../models/activity.model';
import { ActivityRequest } from '../../models/admin/activity-request.model';
import { UserSearchFilters } from '../../models/admin/user-search-filters.model';
import { UserResponse } from '../../models/user/user-response.model';
import { ChangeRoleReqRes } from '../../models/admin/change-role-req-res.model';
import { DeleteUserRequest } from '../../models/admin/delete-user-request.model';

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

  private usersSubject = new BehaviorSubject<UserResponse[]>([]);

  private filtersChangeSubject = new Subject<void>();

  getLoading(): Observable<boolean> {
    return this.loadingSubject.asObservable();
  }

  getApprovalsSubject() {
    return this.approvalsSubject.asObservable();
  }

  getUsersSubject() {
    return this.usersSubject.asObservable();
  }

  emitFiltersChange() {
    this.filtersChangeSubject.next();
  }

  getFiltersChangeSubject() {
    return this.filtersChangeSubject.asObservable();
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

  getActivities(): Observable<Activity[]> {
    this.loadingSubject.next(true);
    return this.http.get<Activity[]>(`${this.apiUrl}/activities`).pipe(
      finalize(() => this.loadingSubject.next(false)),
    );
  }

  addActivity(req: ActivityRequest): Observable<Activity> {
    return this.http.post<Activity>(`${this.apiUrl}/activities`, req).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie dodano aktywność.', type: 'success', duration: 3000 });
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  editActivity(req: Partial<ActivityRequest>, id: string): Observable<Activity> {
    return this.http.patch<Activity>(`${this.apiUrl}/activities/${id}`, req).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie edytowano aktywność.', type: 'success', duration: 3000 });
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  deleteActivity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/activities/${id}`).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie usunięto aktywność.', type: 'success', duration: 3000 });
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  getUsers(page: number, size: number, userSearchFilters: UserSearchFilters): Observable<PageResponse<UserResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())

    if (userSearchFilters.role) params = params.set('role', userSearchFilters.role);

    if (userSearchFilters.firstName) params = params.set('firstName', userSearchFilters.firstName);

    if (userSearchFilters.lastName) params = params.set('lastName', userSearchFilters.lastName);

    this.loadingSubject.next(true);
    return this.http.get<PageResponse<UserResponse>>(`${this.apiUrl}/users`, { params }).pipe(
      tap(res => this.usersSubject.next(res.content)),
      finalize(() => this.loadingSubject.next(false)),
    );
  }

  changeUserRole(req: ChangeRoleReqRes, id: string): Observable<ChangeRoleReqRes> {
    return this.http.patch<ChangeRoleReqRes>(`${this.apiUrl}/users/${id}`, req).pipe(
      tap(() => {
        const currUsers: UserResponse[] = this.usersSubject.getValue();
        const userIndex = currUsers.findIndex(user => user.id === id);

        currUsers[userIndex].role = req.role;

        this.usersSubject.next(currUsers);
      }),
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie zmieniono rolę użytkownika.', type: 'success', duration: 3000 });
      }),
      catchError(err => {
        return this.handleError(err);
      })
    );
  }

  deleteUser(req: DeleteUserRequest, id: string): Observable<void> {
    let params = new HttpParams()
      .set('message', req.message);

    return this.http.delete<void>(`${this.apiUrl}/users/${id}`, { params }).pipe(
      tap(() => {
        const currUsers: UserResponse[] = this.usersSubject.getValue();
        const newUsers = currUsers.filter(user => user.id !== id);

        this.usersSubject.next(newUsers);
      }),
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie usunięto użytkownika.', type: 'success', duration: 3000 });
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
