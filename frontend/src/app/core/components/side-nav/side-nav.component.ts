import { map, Observable, Subscription, tap } from 'rxjs';
import { AuthService } from './../../services/auth/auth.service';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { BreakpointObserver } from '@angular/cdk/layout';
import { NavItem, adminHeaders, doctorHeaders, unverifiedDoctorHeaders, userHeaders } from '../../constants/headers';
import { Store } from '@ngrx/store';
import { Roles } from '../../enums/roles.enum';
import { ROUTE_TITLES } from '../../constants/route-titles';
import { UserResponse } from '../../models/user/user-response.model';

@Component({
  selector: 'app-side-nav',
  templateUrl: './side-nav.component.html',
  styleUrl: './side-nav.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SideNavComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);

  private readonly authService = inject(AuthService);

  private readonly router = inject(Router);

  private readonly observer = inject(BreakpointObserver);

  private readonly cdRef = inject(ChangeDetectorRef);

  navItems$!: Observable<NavItem[]>;

  settingsItem: NavItem = {
    title:'Ustawienia',
    icon: 'settings',
    route: '/user/settings'
  };

  isMobile = signal<boolean>(false);

  isCollapsed = signal<boolean>(false);

  isOpended = computed(() => this.isMobile() ? !this.isCollapsed() : true);

  navWidth = computed(() => this.isCollapsed() ? '56px' : '240px');

  user$: Observable<UserResponse> = this.store.select('user');

  subscriptions: Subscription[] = [];

  pageTitle = signal<string>('');

  ngOnInit(): void {
    this.observer.observe('(max-width: 768px)').subscribe(res => {
      this.isMobile.set(res.matches);
      this.isCollapsed.set(res.matches);
    });

    this.pageTitle.set(ROUTE_TITLES[this.router.url.slice(1)]);

    this.navItems$ = this.user$.pipe(
      map(user => {
        switch (user.role) {
          case Roles.ADMIN:
          case Roles.HEAD_ADMIN:
            this.settingsItem.route = '/admin/settings';
            return adminHeaders;
          case Roles.DOCTOR:
            this.settingsItem.route = '/doctor/settings';
            if (user.isDoctorVerified) {
              return doctorHeaders;
            }
            return unverifiedDoctorHeaders;
          case Roles.USER:
            this.settingsItem.route = '/user/settings';
            return userHeaders;
          default:
            return [];
        }
      })
    )
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  toggleMenu() {
    this.isCollapsed.set(!this.isCollapsed());
    setTimeout(() => {
      this.cdRef.detectChanges();
    }, 100);
  }

  itemClicked() {
    if (this.isMobile()) {
      this.toggleMenu();
    }


    setTimeout(() => {
      this.pageTitle.set(ROUTE_TITLES[this.router.url.slice(1)]);
    })
  }

  logout() {
    this.authService.logout().pipe(
      tap(() => {
        this.router.navigate(['login']);
      })
    ).subscribe();
  }
}
