import { APP_INITIALIZER, NgModule, isDevMode } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { Store, StoreModule } from '@ngrx/store';
import { MyMaterialModule } from './material';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth/auth.interceptor';
import { provideNativeDateAdapter } from '@angular/material/core';
import { Router } from '@angular/router';
import { httpErrorInterceptor } from './core/interceptors/http-error/http-error.interceptor';
import { userReducer } from './core/state/user/user.reducer';
import { activitiesReducer } from './core/state/activities/activities.reducer';
import { citiesReducer } from './core/state/cities/cities.reducer';
import { parametersReducer } from './core/state/parameters/parameters.reducer';
import { specializationsReducer } from './core/state/specializations/specializations.reducer';
import { userParametersReducer } from './core/state/user-parameters/user-parameters.reducer';
import { configActions } from './core/state/config/config.actions';
import { EffectsModule, Actions, ofType } from '@ngrx/effects';
import { AppState } from './core/state/app.state';
import * as configEffects from './core/state/config/config.effects';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { CoreModule } from './core/core.module';
import { firstValueFrom, of, take } from 'rxjs';

function initializeAppFactory(store: Store, router: Router, actions$: Actions): () => void {
  return async () => {
    store.dispatch(configActions.load());

    try {
      await firstValueFrom(
        actions$.pipe(
          ofType(configActions.loadSuccess, configActions.loadError),
          take(1),
        )
      );
    } catch (error) {
      console.error('Error loading config', error);
      return Promise.reject();
    }
  }
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MyMaterialModule,
    CoreModule,
    StoreModule.forRoot<AppState>({
      user: userReducer,
      activities: activitiesReducer,
      cities: citiesReducer,
      parameters: parametersReducer,
      specializations: specializationsReducer,
      userParameters: userParametersReducer,
    }, {}),
    EffectsModule.forRoot([configEffects]),
    StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: !isDevMode(), autoPause: true, trace: true, traceLimit: 75 }),
  ],
  providers: [
    provideAnimationsAsync(),
    provideHttpClient(
      withInterceptors(
        [authInterceptor, httpErrorInterceptor]
      )
    ),
    provideNativeDateAdapter(),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAppFactory,
      multi: true,
      deps: [Store, Router, Actions],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
