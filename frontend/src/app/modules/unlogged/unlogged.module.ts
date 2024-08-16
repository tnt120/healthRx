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
import { TextSummaryRowComponent } from './components/text-summary-row/text-summary-row.component';
import { UnloggedHeaderComponent } from './components/unlogged-header/unlogged-header.component';
import { CoreModule } from '../../core/core.module';


@NgModule({
  declarations: [
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    VerificationComponent,
    BackButtonComponent,
    TextSummaryRowComponent,
    UnloggedHeaderComponent,
  ],
  imports: [
    CommonModule,
    UnloggedRoutingModule,
    SharedModule,
    MyMaterialModule,
    CoreModule
  ]
})
export class UnloggedModule { }
