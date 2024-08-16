import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ApprovalsComponent } from './pages/approvals/approvals.component';
import { PatientsDashboardComponent } from './pages/patients-dashboard/patients-dashboard.component';
import { DoctorMessagesComponent } from './pages/doctor-messages/doctor-messages.component';
import { MainLayoutComponent } from '../../core/components/main-layout/main-layout.component';
import { SettingsComponent } from '../../shared/components/settings/settings.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'approvals',
        component: ApprovalsComponent
      },
      {
        path: 'patients',
        component: PatientsDashboardComponent
      },
      {
        path: 'messages',
        component: DoctorMessagesComponent
      },
      {
        path: 'settings',
        component: SettingsComponent
      },
      {
        path: '',
        redirectTo: 'patients',
        pathMatch: 'full'
      },
      {
        path: '**',
        redirectTo: 'patients' // w przyszłości dodać błąd że strona nie istnieje
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DoctorRoutingModule { }
