import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environments';
import { HttpClient, HttpParams } from '@angular/common/http';
import { SortOption } from '../../models/sort-option.model';
import { map, Observable, Subject } from 'rxjs';
import { PageResponse } from '../../models/page-response.model';
import { UserActivityResponse } from '../../models/user-activity-response.model';
import { UserActivityRequest } from '../../models/user-activity-request.model';

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

  private filterChange = new Subject<boolean>();

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

    return this.http.get<PageResponse<UserActivityResponse>>(`${this.apiUrl}/user`, { params }).pipe(
      map(res => ({
        ...res,
        content: res.content.map(activity => ({
          ...activity,
          activityTime: new Date(activity.activityTime)
        }))
      }))
    );
  }

  addUserActivity(req: UserActivityRequest): Observable<UserActivityResponse> {
    return this.http.post<UserActivityResponse>(`${this.apiUrl}/user`, req).pipe(
      map(activity => ({
        ...activity,
        activityTime: new Date(activity.activityTime)
      }))
    );
  }

  editUserActivity(id: string, req: UserActivityRequest): Observable<UserActivityResponse> {
    return this.http.put<UserActivityResponse>(`${this.apiUrl}/user/${id}`, req).pipe(
      map(activity => ({
        ...activity,
        activityTime: new Date(activity.activityTime)
      }))
    );
  }

  deleteUserActivity(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/user/${id}`);
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
