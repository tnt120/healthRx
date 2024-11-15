import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideMockStore, MockStore } from '@ngrx/store/testing';
import { AppState } from '../../state/app.state';
import { userInitialState } from '../../state/user/user.reducer';
import { activitiesInitialState } from '../../state/activities/activities.reducer';
import { initialNotificationsSettingsState } from '../../state/notifications-settings/notifications-settings.reducer';

describe('AuthService', () => {
  let service: AuthService;
  const initialState: AppState = {
    user: userInitialState,
    activities: activitiesInitialState,
    cities: [],
    parameters: [],
    specializations: [],
    userParameters: [],
    notificationsSettings: initialNotificationsSettingsState,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
        provideMockStore({ initialState }),
      ]
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
