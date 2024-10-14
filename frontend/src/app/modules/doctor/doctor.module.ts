import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { DoctorRoutingModule } from './doctor-routing.module';
import { DoctorMessagesComponent } from './pages/doctor-messages/doctor-messages.component';
import { PatientsDashboardComponent } from './pages/patients-dashboard/patients-dashboard.component';
import { ApprovalsComponent } from './pages/approvals/approvals.component';
import { SharedModule } from '../../shared/shared.module';
import { MyMaterialModule } from '../../material';
import { CoreModule } from '../../core/core.module';
import { UnverifiedDoctorComponent } from './pages/unverified-doctor/unverified-doctor.component';
import { ReVerifyComponent } from './components/re-verify/re-verify.component';
import { PreviewDoctorDataComponent } from './components/preview-doctor-data/preview-doctor-data.component';


@NgModule({
  declarations: [
    DoctorMessagesComponent,
    PatientsDashboardComponent,
    ApprovalsComponent,
    UnverifiedDoctorComponent,
    ReVerifyComponent,
    PreviewDoctorDataComponent,
  ],
  imports: [
    CommonModule,
    DoctorRoutingModule,
    SharedModule,
    MyMaterialModule,
    CoreModule
  ]
})
export class DoctorModule { }
