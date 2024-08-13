import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { StoreModule } from '@ngrx/store';
import { MyMaterialModule } from './material';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth/auth.interceptor';
import { provideNativeDateAdapter } from '@angular/material/core';
import { UserService } from './core/services/user/user.service';
import { catchError, EMPTY, Observable, tap, throwError } from 'rxjs';
import { Config } from './core/models/user/config.model';
import { Router } from '@angular/router';
import { httpErrorInterceptor } from './core/interceptors/http-error/http-error.interceptor';

function initializeAppFactory(userService: UserService, router: Router): () => Observable<Config> {
  return () => {

    // odkomentowac po testowaniu i dodaniu routingów
    // const path = router.url;
    // const unloggedPaths = ['/login', '/register', ''];

    // if (!unloggedPaths.includes(path)) {
    //   return EMPTY;
    // }

    return userService.getInitAndConfig().pipe(
      tap((config) => {
        console.log('Config initialized', config);
      }),
      catchError((err) => {
        console.error('Error while initializing app', err);
        return EMPTY;
      })
    );
  };
}

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MyMaterialModule,
    StoreModule.forRoot({}, {}),
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
      deps: [UserService, Router],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
