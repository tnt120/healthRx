import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ParametersDashboardComponent } from './pages/parameters-dashboard/parameters-dashboard.component';
import { CabinetDashboardComponent } from './pages/cabinet-dashboard/cabinet-dashboard.component';
import { CabinetAddComponent } from './pages/cabinet-add/cabinet-add.component';
import { ActivitesDashboardComponent } from './pages/activites-dashboard/activites-dashboard.component';
import { SearchDoctorComponent } from './pages/search-doctor/search-doctor.component';
import { UserMessagesComponent } from './pages/user-messages/user-messages.component';
import { SettingsComponent } from './pages/settings/settings.component';

const routes: Routes = [
  {
    path: 'parameters',
    component: ParametersDashboardComponent
  },
  {
    path: 'cabinet',
    component: CabinetDashboardComponent
  },
  {
    path: 'cabinet/add',
    component: CabinetAddComponent
  },
  {
    path: 'activities',
    component: ActivitesDashboardComponent
  },
  {
    path: 'doctors',
    component: SearchDoctorComponent
  },
  {
    path: 'messages',
    component: UserMessagesComponent
  },
  {
    path: 'settings',
    component: SettingsComponent
  },
  {
    path: '',
    redirectTo: 'parameters',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: 'parameters' // w przyszłości dodać błąd że strona nie istnieje
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
