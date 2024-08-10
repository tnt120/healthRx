import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { StoreModule } from '@ngrx/store';
import { MyMaterialModule } from './material';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './core/interceptors/auth/auth.interceptor';


@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MyMaterialModule,
    StoreModule.forRoot({}, {})
  ],
  providers: [
    provideAnimationsAsync(),
    provideHttpClient(
      withInterceptors([authInterceptor])
    )
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
