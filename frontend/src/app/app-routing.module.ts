import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    loadChildren: () => import('./modules/unlogged/unlogged.module').then(m => m.UnloggedModule)
  },
  {
    path: 'user',
    loadChildren: () => import('./modules/user/user.module').then(m => m.UserModule)
  },
  {
    path: 'doctor',
    loadChildren: () => import('./modules/doctor/doctor.module').then(m => m.DoctorModule)
  },
  {
    path: 'admin',
    loadChildren: () => import('./modules/admin/admin.module').then(m => m.AdminModule)
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
