import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { SharedModule } from '../../shared/shared.module';
import { MyMaterialModule } from '../../material';
import { CoreModule } from '../../core/core.module';


@NgModule({
  declarations: [
    DashboardComponent
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
