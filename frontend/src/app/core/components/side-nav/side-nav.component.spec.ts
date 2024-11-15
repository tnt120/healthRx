import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideNavComponent } from './side-nav.component';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';
import { AppState } from '../../state/app.state';
import { userInitialState } from '../../state/user/user.reducer';
import { activitiesInitialState } from '../../state/activities/activities.reducer';
import { initialNotificationsSettingsState } from '../../state/notifications-settings/notifications-settings.reducer';
import { provideMockStore } from '@ngrx/store/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('SideNavComponent', () => {
  let component: SideNavComponent;
  let fixture: ComponentFixture<SideNavComponent>;
  const initialState: AppState = {
    user: userInitialState,
    activities: activitiesInitialState,
    cities: [],
    parameters: [],
    specializations: [],
    userParameters: [],
    notificationsSettings: initialNotificationsSettingsState,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SideNavComponent],
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideMockStore({ initialState }),
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SideNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
