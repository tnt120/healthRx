import { filter, Observable, Subscription, tap } from 'rxjs';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { AdminService } from '../../../../core/services/admin/admin.service';
import { Pagination } from '../../../../core/models/pagination.model';
import { basePagination } from '../../../../core/constants/paginations-options';
import { PageEvent } from '@angular/material/paginator';
import { UserSearchFilters } from '../../../../core/models/admin/user-search-filters.model';
import { UserResponse } from '../../../../core/models/user/user-response.model';
import { ChangeRoleReqRes } from '../../../../core/models/admin/change-role-req-res.model';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { RoleChangeDialogComponent } from '../../components/role-change-dialog/role-change-dialog.component';
import { Store } from '@ngrx/store';
import { DeleteUserDialogComponent } from '../../components/delete-user-dialog/delete-user-dialog.component';

@Component({
  selector: 'app-users-manage',
  templateUrl: './users-manage.component.html',
  styleUrl: './users-manage.component.scss'
})
export class UsersManageComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);

  private readonly adminService = inject(AdminService);

  private readonly dialog = inject(MatDialog);

  private subscriptions = new Subscription();

  user$: Observable<UserResponse> = this.store.select('user');

  users$ = this.adminService.getUsersSubject();

  isLoading$ = this.adminService.getLoading();

  filterChange$?: Observable<void>;

  pagination: Pagination = {...basePagination };

  filters = signal<UserSearchFilters>({
    role: null,
    firstName: null,
    lastName: null,
  });

  ngOnInit(): void {
    this.filterChange$ = this.adminService.getFiltersChangeSubject()
      .pipe(
        tap((res) => this.getUsers())
      );

    this.getUsers();
  }

  private getUsers() {
    this.adminService.getUsers(this.pagination.pageIndex, this.pagination.pageSize, this.filters()).pipe(
      tap(res => {
        this.pagination.totalElements = res.totalElements;
      })
    ).subscribe();
  }

  protected onSearch(filters: UserSearchFilters): void {
    this.filters.set(filters);
    this.adminService.emitFiltersChange();
  }

  protected handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.adminService.emitFiltersChange();
  }

  protected onRoleChange(user: UserResponse): void {
    const data: UserResponse = user;

    const dialogRef = this.dialog.open(RoleChangeDialogComponent, { data, minWidth: '350px' });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap(res => this.handleChangeRole({ role: res }, user.id))
      ).subscribe()
    )
  }

  protected onDeleteUser(user: UserResponse): void {
    const data: UserResponse = user;

    const dialogRef = this.dialog.open(DeleteUserDialogComponent, { data });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap((res) => this.handleDeleteUser(user.id, res))
      ).subscribe()
    )
  }

  private handleChangeRole(req: ChangeRoleReqRes, id: string): void {
    this.adminService.changeUserRole(req, id).subscribe();
  }

  private handleDeleteUser(id: string, message: string): void {
    this.adminService.deleteUser({ message }, id).subscribe();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
