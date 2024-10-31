import { Component, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { map, Observable } from 'rxjs';
import { UserResponse } from '../../../../core/models/user/user-response.model';

@Component({
  selector: 'app-user-statistics',
  templateUrl: './user-statistics.component.html',
  styleUrl: './user-statistics.component.scss',
})
export class UserStatisticsComponent  {
  private readonly store = inject(Store);

  userHeight$: Observable<number>;

  constructor() {
    this.userHeight$ = this.store.select('user').pipe(
      map((user: UserResponse) => user.height ?? 0)
    )
  }
}
