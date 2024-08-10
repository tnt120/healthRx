import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UnloggedRoutingModule } from './unlogged-routing.module';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { VerificationComponent } from './pages/verification/verification.component';
import { SharedModule } from '../../shared/shared.module';
import { BackButtonComponent } from './components/back-button/back-button.component';
import { MyMaterialModule } from '../../material';


@NgModule({
  declarations: [
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    VerificationComponent,
    BackButtonComponent
  ],
  imports: [
    CommonModule,
    UnloggedRoutingModule,
    SharedModule,
    MyMaterialModule
  ]
})
export class UnloggedModule { }
