import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { adminGuard, doctorGuard, notAuthorizedGuard, userGuard } from './core/guards/roleFactory/role-factory.guard';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./modules/unlogged/unlogged.module').then(m => m.UnloggedModule),
    canActivate: [notAuthorizedGuard]
  },
  {
    path: 'user',
    loadChildren: () => import('./modules/user/user.module').then(m => m.UserModule),
    canActivate: [userGuard]
  },
  {
    path: 'doctor',
    loadChildren: () => import('./modules/doctor/doctor.module').then(m => m.DoctorModule),
    canActivate: [doctorGuard]
  },
  {
    path: 'admin',
    loadChildren: () => import('./modules/admin/admin.module').then(m => m.AdminModule),
    canActivate: [adminGuard]
  },
  {
    path: '**',
    redirectTo: '' // w przyszłości dodac że strona nie istnieje
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
