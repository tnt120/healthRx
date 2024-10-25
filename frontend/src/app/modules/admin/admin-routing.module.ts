import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MainLayoutComponent } from '../../core/components/main-layout/main-layout.component';
import { SettingsComponent } from '../../shared/components/settings/settings.component';
import { DoctorApprovalsComponent } from './pages/doctor-approvals/doctor-approvals.component';
import { ParametersManageComponent } from './pages/parameters-manage/parameters-manage.component';
import { ActivitiesManageComponent } from './pages/activities-manage/activities-manage.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      {
        path: 'dashboard',
        component: DashboardComponent
      },
      {
        path: 'approvals',
        component: DoctorApprovalsComponent
      },
      {
        path: 'parameters-manage',
        component: ParametersManageComponent
      },
      {
        path: 'activities-manage',
        component: ActivitiesManageComponent
      },
      {
        path: 'settings',
        component: SettingsComponent
      },
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: '**',
        redirectTo: '' // w przyszłości dodać błąd że strona nie istnieje
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
