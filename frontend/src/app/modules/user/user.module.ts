import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserRoutingModule } from './user-routing.module';
import { CabinetDashboardComponent } from './pages/cabinet-dashboard/cabinet-dashboard.component';
import { CabinetAddComponent } from './pages/cabinet-add/cabinet-add.component';
import { CabinetCalendarComponent } from './pages/cabinet-calendar/cabinet-calendar.component';
import { SearchDoctorComponent } from './pages/search-doctor/search-doctor.component';
import { UserMessagesComponent } from './pages/user-messages/user-messages.component';
import { ParametersDashboardComponent } from './pages/parameters-dashboard/parameters-dashboard.component';
import { ActivitesDashboardComponent } from './pages/activites-dashboard/activites-dashboard.component';
import { SharedModule } from '../../shared/shared.module';
import { MyMaterialModule } from '../../material';
import { CoreModule } from '../../core/core.module';
import { UserStatisticsComponent } from './pages/user-statistics/user-statistics.component';
import { EditParameterMonitorDialogComponent } from './components/edit-parameter-monitor-dialog/edit-parameter-monitor-dialog.component';
import { DrugItemComponent } from './components/drug-item/drug-item.component';
import { TakeDrugMonitorDialogComponent } from './components/take-drug-monitor-dialog/take-drug-monitor-dialog.component';
import { UserDrugsDetailsComponent } from './components/user-drugs-details/user-drugs-details.component';
import { EditUserDrugDialogComponent } from './components/edit-user-drug-dialog/edit-user-drug-dialog.component';
import { EditDrugStockDialogComponent } from './components/edit-drug-stock-dialog/edit-drug-stock-dialog.component';
import { DoctorCardComponent } from './components/doctor-card/doctor-card.component';
import { SearchDoctorsFilterPanelComponent } from './components/search-doctors-filter-panel/search-doctors-filter-panel.component';
import { MyDoctorsComponent } from './pages/my-doctors/my-doctors.component';
import { ParameterStatisticsComponent } from './components/parameter-statistics/parameter-statistics.component';
import { DrugStatisticsComponent } from './components/drug-statistics/drug-statistics.component';
import { ParameterChartComponent } from './components/parameter-statistics/parameter-chart/parameter-chart.component';
import { DrugChartComponent } from './components/drug-statistics/drug-chart/drug-chart.component';
import { PopularActivitiesComponent } from './components/popular-activities/popular-activities.component';
import { UserActivitiesComponent } from './components/user-activities/user-activities.component';
import { ActivityTileComponent } from './components/activity-tile/activity-tile.component';
import { ActivityEditTileComponent } from './components/activity-edit-tile/activity-edit-tile.component';
import { ManageUserActivityDialogComponent } from './components/manage-user-activity-dialog/manage-user-activity-dialog.component';
import { ActivityStatisticsComponent } from './components/activity-statistics/activity-statistics.component';
import { ActivityChartComponent } from './components/activity-statistics/activity-chart/activity-chart.component';


@NgModule({
  declarations: [
    CabinetDashboardComponent,
    CabinetAddComponent,
    CabinetCalendarComponent,
    SearchDoctorComponent,
    UserMessagesComponent,
    ParametersDashboardComponent,
    ActivitesDashboardComponent,
    UserStatisticsComponent,
    EditParameterMonitorDialogComponent,
    DrugItemComponent,
    TakeDrugMonitorDialogComponent,
    UserDrugsDetailsComponent,
    EditUserDrugDialogComponent,
    EditDrugStockDialogComponent,
    DoctorCardComponent,
    SearchDoctorsFilterPanelComponent,
    MyDoctorsComponent,
    ParameterStatisticsComponent,
    DrugStatisticsComponent,
    ParameterChartComponent,
    DrugChartComponent,
    PopularActivitiesComponent,
    UserActivitiesComponent,
    ActivityTileComponent,
    ActivityEditTileComponent,
    ManageUserActivityDialogComponent,
    ActivityStatisticsComponent,
    ActivityChartComponent,
  ],
  imports: [
    CommonModule,
    UserRoutingModule,
    SharedModule,
    MyMaterialModule,
    CoreModule
  ]
})
export class UserModule { }
