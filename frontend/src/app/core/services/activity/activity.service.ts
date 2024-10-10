import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SortOption } from '../../models/sort-option.model';
import { BehaviorSubject, catchError, finalize, map, Observable, Subject, tap, throwError } from 'rxjs';
import { PageResponse } from '../../models/page-response.model';
import { UserActivityResponse } from '../../models/user-activity-response.model';
import { UserActivityRequest } from '../../models/user-activity-request.model';
import { CustomSnackbarService } from '../custom-snackbar/custom-snackbar.service';

export interface AcitivtySearchParams {
  activityId?: string | null;
  startDate: string | null;
  endDate: string | null;
}


@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  private readonly apiUrl = `${environment.apiUrl}/activities`;

  private readonly http = inject(HttpClient);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  private filterChange = new Subject<boolean>();

  private loadingActivitiesSubject = new BehaviorSubject<Map<'today' | 'all', boolean>>(new Map([['today', false], ['all', false]]));

  getLoadingActivityState(type: 'today' | 'all'): Observable<boolean> {
    return this.loadingActivitiesSubject.asObservable().pipe(
      map(stateMap => stateMap.get(type) || false)
    );
  }

  setLoadingActivityState(type: 'today' | 'all', loading: boolean) {
    const currState = this.loadingActivitiesSubject.getValue();
    const updatedState = new Map(currState).set(type, loading);

    this.loadingActivitiesSubject.next(updatedState);
  }

  getFilterChange() {
    return this.filterChange.asObservable();
  }

  emitFilterChange(isTodaysActivity = false) {
    this.filterChange.next(isTodaysActivity);
  }

  getActivities(page: number, size: number, sort: SortOption, search?: AcitivtySearchParams): Observable<PageResponse<UserActivityResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort.sortBy)
      .set('order', sort.order);

    if (search?.activityId) params = params.set('activityId', search.activityId);
    if (search?.startDate) params = params.set('startDate', search.startDate);
    if (search?.endDate) params = params.set('endDate', search.endDate);

    const type: 'today' | 'all' = search?.startDate?.substring(0, 10) === search?.endDate?.substring(0, 10) ? 'today' : 'all';

    this.setLoadingActivityState(type, true);
    return this.http.get<PageResponse<UserActivityResponse>>(`${this.apiUrl}/user`, { params }).pipe(
      map(res => ({
        ...res,
        content: res.content.map(activity => ({
          ...activity,
          activityTime: new Date(activity.activityTime)
        }))
      })),
      finalize(() => this.setLoadingActivityState(type, false))
    );
  }

  addUserActivity(req: UserActivityRequest): Observable<UserActivityResponse> {
    return this.http.post<UserActivityResponse>(`${this.apiUrl}/user`, req).pipe(
      map(activity => ({
        ...activity,
        activityTime: new Date(activity.activityTime)
      })),
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie dodano aktywność.', type: 'success', duration: 2500 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystawił błąd podczas dodwania aktywności.', type: 'error', duration: 2500 });
        return throwError(() => err)
      })
    );
  }

  editUserActivity(id: string, req: UserActivityRequest): Observable<UserActivityResponse> {
    return this.http.put<UserActivityResponse>(`${this.apiUrl}/user/${id}`, req).pipe(
      map(activity => ({
        ...activity,
        activityTime: new Date(activity.activityTime)
      })),
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie edytowano aktywność.', type: 'success', duration: 2500 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystawił błąd podczas edytowania aktywności.', type: 'error', duration: 2500 });
        return throwError(() => err)
      })
    );
  }

  deleteUserActivity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/user/${id}`).pipe(
      tap(() => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Sukces', message: 'Pomyślnie usunięto aktywność.', type: 'success', duration: 2500 });
      }),
      catchError((err) => {
        this.customSnackbarService.openCustomSnackbar({ title: 'Błąd', message: 'Wystawił błąd podczas usuwania aktywności.', type: 'error', duration: 2500 });
        return throwError(() => err)
      })
    );
  }

  getActivityDuration(duration: number): string {
    const hours = Math.floor(duration / 60);
    const minutes = duration % 60;

    return `${hours}h ${minutes}m`;
  }

  getIcon(activityName: string): string {
    const lowerName = activityName.toLowerCase();

    switch (true) {
      case lowerName.includes('piłka nożna'):
        return 'sports_soccer';
      case lowerName.includes('koszykówka'):
        return 'sports_basketball';
      case lowerName.includes('siatkówka'):
        return 'sports_volleyball';
      case lowerName.includes('rower'):
        return 'directions_bike';
      case lowerName.includes('tenis'):
        return 'sports_tennis';
      case lowerName.includes('siłownia'):
        return 'fitness_center';
      case lowerName.includes('bieganie'):
        return 'sprint';
      case lowerName.includes('pływanie'):
        return 'pool';
      default:
        return 'directions_walk';
    }
  }
}
