import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { map, Observable, Subscription } from 'rxjs';
import { Roles } from '../../../core/enums/roles.enum';
import { UserResponse } from '../../../core/models/user/user-response.model';
import { BreakpointObserver } from '@angular/cdk/layout';
import { ActivatedRoute } from '@angular/router';
import { NotificationsData } from '../../../core/models/notifications-data.model';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.scss'
})
export class SettingsComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);

  private readonly observer = inject(BreakpointObserver);

  private readonly route = inject(ActivatedRoute);

  subscriptions: Subscription[] = [];

  role!: Roles;

  user$: Observable<UserResponse> = this.store.select('user');

  tabPadding: string = '10px 24px';

  parametersChange = false;

  userData$: Observable<UserResponse> = this.store.select('user');

  notificationsSettings$: Observable<NotificationsData> = this.store.select('notificationsSettings');

  ngOnInit(): void {
    this.parametersChange = this.route.snapshot?.queryParams['parametersChange'];

    this.subscriptions.push(this.user$.pipe(
      map(user => user.role)
    ).subscribe(
      role => this.role = role
    ));

    this.observer.observe('(max-width: 1024px)').subscribe(res => {
      res.matches ? this.tabPadding = '24px 10px' : this.tabPadding = '10px 24px';
    });

  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }
}
