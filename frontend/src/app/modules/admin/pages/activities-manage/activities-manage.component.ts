import { filter, map, Observable, Subscription, tap } from 'rxjs';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { AdminService } from '../../../../core/services/admin/admin.service';
import { MatDialog } from '@angular/material/dialog';
import { Activity } from '../../../../core/models/activity.model';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { ActivityRequest } from '../../../../core/models/admin/activity-request.model';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { ActivityManageDialogComponent, ActivityManageDialogData } from '../../components/activity-manage-dialog/activity-manage-dialog.component';

@Component({
  selector: 'app-activities-manage',
  templateUrl: './activities-manage.component.html',
  styleUrl: './activities-manage.component.scss'
})
export class ActivitiesManageComponent implements OnInit, OnDestroy {
  private readonly adminService = inject(AdminService);

  private readonly dialog = inject(MatDialog);

  private subscriptions = new Subscription();

  protected activities$?: Observable<Activity[]>;

  protected isLoading$?: Observable<boolean>;

  activities = signal<Activity[] | null>(null);

  columns: TableColumn[] = [];

  ngOnInit(): void {
    this.activities$ = this.adminService.getActivities().pipe(
      tap(res => this.activities.set(res))
    );
    this.isLoading$ = this.adminService.getLoading();
    this.fillColumns();
  }

  private fillColumns(): void {
    this.columns = [
      { title: 'Nazwa', displayedColumn: 'name' },
      { title: 'Wskaźnik MET', displayedColumn: 'metFactor' },
      { title: 'Popularna aktywność', displayedColumn: 'isPopular' },
    ]
  }

  onAdd(): void {
    const data: ActivityManageDialogData = {};

    const dialogRef = this.dialog.open(ActivityManageDialogComponent, { data, minWidth: '350px' });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap((res: ActivityRequest) => this.handleActivityAdd(res))
      ).subscribe()
    )
  }

  onEdit(activity: Activity): void {
    const data: ActivityManageDialogData = { activity };

    const dialogRef = this.dialog.open(ActivityManageDialogComponent, { data, minWidth: '350px' });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        map(res => {
          const req: Partial<ActivityRequest> = {
            name: res.name === activity.name ? undefined : res.name,
            metFactor: res.metFactor === activity.metFactor ? undefined : res.metFactor,
            isPopular: res.isPopular === activity.isPopular ? undefined : res.isPopular,
          };

          return req;
        }),
        tap(req => this.handleActivityEdit(req, activity.id))
      ).subscribe()
    );
  }

  onDelete(activity: Activity): void {
    const data: ConfirmationDialogData = {
      title: 'Usuwanie aktywności',
      message: `Czy na pewno chcesz usunąć aktywność "${activity.name}"?`,
      acceptButton: 'Usuń',
    }

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, { data });

    this.subscriptions.add(
      dialogRef.afterClosed().pipe(
        filter(res => res),
        tap(() => this.handleActivityDelete(activity.id))
      ).subscribe()
    );
  }

  private handleActivityAdd(res: ActivityRequest): void {
    this.adminService.addActivity(res).pipe(
      tap(activity => {
        this.activities.set([...this.activities() || [], activity]);
      })
    ).subscribe();
  }

  private handleActivityEdit(res: Partial<ActivityRequest>, id: string): void {
    this.adminService.editActivity(res, id).pipe(
      tap(activity => {
        this.activities.set(this.activities()?.map(a => a.id === id ? activity : a) || []);
      })
    ).subscribe();
  }

  private handleActivityDelete(id: string): void {
    this.adminService.deleteActivity(id).pipe(
      tap(() => {
        this.activities.set(this.activities()?.filter(a => a.id !== id) || []);
      })
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
