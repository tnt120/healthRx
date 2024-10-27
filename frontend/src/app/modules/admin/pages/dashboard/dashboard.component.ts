import { Component, inject } from '@angular/core';
import { AdminService } from '../../../../core/services/admin/admin.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent {
  private readonly adminService = inject(AdminService);

  dashboardData$ = this.adminService.getDashboardData();

  isLoading$ = this.adminService.getLoading();
}
