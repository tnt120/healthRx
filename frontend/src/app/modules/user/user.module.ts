import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserRoutingModule } from './user-routing.module';
import { CabinetDashboardComponent } from './pages/cabinet-dashboard/cabinet-dashboard.component';
import { CabinetAddComponent } from './pages/cabinet-add/cabinet-add.component';
import { CabinetCalendarComponent } from './pages/cabinet-calendar/cabinet-calendar.component';
import { SearchDoctorComponent } from './pages/search-doctor/search-doctor.component';
import { UserMessagesComponent } from './pages/user-messages/user-messages.component';
import { SettingsComponent } from './pages/settings/settings.component';
import { ParametersDashboardComponent } from './pages/parameters-dashboard/parameters-dashboard.component';
import { ActivitesDashboardComponent } from './pages/activites-dashboard/activites-dashboard.component';
import { SharedModule } from '../../shared/shared.module';
import { MyMaterialModule } from '../../material';


@NgModule({
  declarations: [
    CabinetDashboardComponent,
    CabinetAddComponent,
    CabinetCalendarComponent,
    SearchDoctorComponent,
    UserMessagesComponent,
    SettingsComponent,
    ParametersDashboardComponent,
    ActivitesDashboardComponent
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    SharedModule,
    MyMaterialModule
  ]
})
export class UserModule { }
