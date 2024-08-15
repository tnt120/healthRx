import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DoctorRoutingModule } from './doctor-routing.module';
import { DoctorMessagesComponent } from './pages/doctor-messages/doctor-messages.component';
import { PatientsDashboardComponent } from './pages/patients-dashboard/patients-dashboard.component';
import { ApprovalsComponent } from './pages/approvals/approvals.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { SharedModule } from '../../shared/shared.module';
import { MyMaterialModule } from '../../material';


@NgModule({
  declarations: [
    DoctorMessagesComponent,
    PatientsDashboardComponent,
    ApprovalsComponent,
    SettingsComponent
  ],
  imports: [
    CommonModule,
    DoctorRoutingModule,
    SharedModule,
    MyMaterialModule
  ]
})
export class DoctorModule { }
