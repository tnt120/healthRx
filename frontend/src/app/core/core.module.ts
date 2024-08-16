import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SideNavComponent } from './components/side-nav/side-nav.component';
import { MyMaterialModule } from '../material';
import { MainLayoutComponent } from './components/main-layout/main-layout.component';
import { RouterModule } from '@angular/router';
import { NavItemComponent } from './components/side-nav/nav-item/nav-item.component';


@NgModule({
  declarations: [
    SideNavComponent,
    MainLayoutComponent,
    NavItemComponent,
  ],
  imports: [
    CommonModule,
    MyMaterialModule,
    RouterModule
  ],
  exports: [
    SideNavComponent,
  ]
})
export class CoreModule { }
