import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { SharedModule } from '../../shared/shared.module';
import { MyMaterialModule } from '../../material';
import { CoreModule } from '../../core/core.module';
import { DoctorApprovalsComponent } from './pages/doctor-approvals/doctor-approvals.component';
import { DoctorApprovalCardComponent } from './components/doctor-approval-card/doctor-approval-card.component';
import { RejectDoctorVerificationDialogComponent } from './components/reject-doctor-verification-dialog/reject-doctor-verification-dialog.component';
import { ParametersManageComponent } from './pages/parameters-manage/parameters-manage.component';
import { ParameterMangeDialogComponent } from './components/parameter-mange-dialog/parameter-mange-dialog.component';


@NgModule({
  declarations: [
    DashboardComponent,
    DoctorApprovalsComponent,
    DoctorApprovalCardComponent,
    RejectDoctorVerificationDialogComponent,
    ParametersManageComponent,
    ParameterMangeDialogComponent
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    SharedModule,
    MyMaterialModule,
    CoreModule
  ]
})
export class AdminModule { }
