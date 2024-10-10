import { Subscription } from 'rxjs';
import { AcitivtySearchParams, ActivityService } from './../../../../core/services/activity/activity.service';
import { Component, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { Pagination } from '../../../../core/models/pagination.model';
import { basePagination } from '../../../../core/constants/paginations-options';
import { SortOption } from '../../../../core/models/sort-option.model';
import { activitiesSortOptions } from '../../../../core/constants/sort-options';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { PageEvent } from '@angular/material/paginator';
import { UserActivityResponse } from '../../../../core/models/user-activity-response.model';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../core/services/date/date.service';
import { DatePipe } from '@angular/common';
import { MatDialog } from '@angular/material/dialog';
import { ManageUserActivityData, ManageUserActivityDialogComponent } from '../manage-user-activity-dialog/manage-user-activity-dialog.component';
import { Activity } from '../../../../core/models/activity.model';
import { UserActivityRequest } from '../../../../core/models/user-activity-request.model';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-user-activities',
  templateUrl: './user-activities.component.html',
  styleUrl: './user-activities.component.scss',
  providers: [DatePipe]
})
export class UserActivitiesComponent implements OnInit, OnDestroy {
  private readonly activityService = inject(ActivityService);

  private readonly dateService = inject(DateService);

  private readonly datePipe = inject(DatePipe);

  private readonly dialog = inject(MatDialog);

  isToday = input.required<boolean>();

  activities = input.required<Activity[]>();

  subscriptions: Subscription[] = [];

  pagination: Pagination = {...basePagination};

  sortOptions: SortOption[] = [...activitiesSortOptions];

  sort: SortOption = this.sortOptions[0];

  tableData = signal<any[]>([]);

  userDrugsDisplayedColumns: TableColumn[] = [];

  userActivities = signal<UserActivityResponse[]>([]);

  searchParams: AcitivtySearchParams = {
    activityId: null,
    startDate: null,
    endDate: null
  }

  dateRangeOptions = [...DateRangeOptions];

  date = signal<{ label: DateRangeType, from: Date | null, to: Date | null }> ({
    label: this.dateRangeOptions[0].value,
    from: null,
    to: null
  });

  ngOnInit(): void {
    if (this.isToday()) {
      this.date.set({
        label: 'today',
        from: this.dateService.getDateRange('today').from,
        to: this.dateService.getDateRange('today').to
      });
    } else {
      this.date.set({
        label: this.dateRangeOptions[0].value,
        from: this.dateService.getDateRange(this.dateRangeOptions[0].value).from,
        to: this.dateService.getDateRange(this.dateRangeOptions[0].value).to
      });
    }

    this.fillColumns();
    this.loadActivities();
    this.subscriptions.push(this.activityService.getFilterChange().subscribe((res) => {
      if (res === this.isToday()) {
        this.loadActivities();
      }
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  loadActivities(): void {
    this.searchParams.startDate = this.datePipe.transform(this.date().from, 'yyyy-MM-dd')! + 'T00:00:00';
    this.searchParams.endDate = this.datePipe.transform(this.date().to, 'yyyy-MM-dd')! + 'T23:59:59'

    this.subscriptions.push(
      this.activityService.getActivities(this.pagination.pageIndex, this.pagination.pageSize, this.sort, this.searchParams).subscribe(res => {
        this.tableData.set(res.content.map(activity => this.setTableRow(activity)));

        this.userActivities.set(res.content);
        this.pagination.totalElements = res.totalElements;
      })
    );
  }

  setTableRow(activity: UserActivityResponse): any {
    return {
      id: activity.id,
      name: activity.activity.name,
      activityTime: this.datePipe.transform(activity.activityTime, 'dd/MM/yyyy HH:mm'),
      duration: this.activityService.getActivityDuration(activity.duration),
      averageHeartRate: activity.averageHeartRate ? activity.averageHeartRate : '-',
      caloriesBurned: activity.caloriesBurned ? activity.caloriesBurned : '-',
    }
  }

  onEdit(activity: any): void {
    const userActivity: UserActivityResponse = this.isToday() ? activity : this.userActivities().find(a => a.id === activity.id);

    const dialogData: ManageUserActivityData = {
      activity: null,
      userActivity: userActivity,
      activities: this.activities()
    };

    const dialogRef = this.dialog.open(ManageUserActivityDialogComponent, { data: dialogData, minWidth: '50vw' });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe((res) => {
        if (res) {
          const req: UserActivityRequest = {
            duration: res.duration,
            activityId: res.activityId,
            activityTime: res.activityTime,
            averageHeartRate: res.averageHeartRate,
            caloriesBurned: res.caloriesBurned
          }

          this.subscriptions.push(
            this.activityService.editUserActivity(userActivity.id, req).subscribe((res) => {
              if (this.isToday()) {
                if (this.datePipe.transform(res.activityTime, 'YYYY/MM/dd') !== this.datePipe.transform(userActivity.activityTime, 'YYYY/MM/dd')) {
                  this.userActivities.set(this.userActivities().filter(a => a.id !== res.id));
                } else {
                  this.userActivities.set(this.userActivities().map(a => a.id === res.id ? res : a));
                }

                this.activityService.emitFilterChange();
              } else {
                this.tableData.set(this.tableData().map(a => a.id === res.id ? this.setTableRow(res) : a));
                this.userActivities.set(this.userActivities().map(a => a.id === res.id ? res : a));

                if (this.dateService.checkIfToday(res.activityTime) || this.dateService.checkIfToday(userActivity.activityTime)) {
                  this.activityService.emitFilterChange(true);
                }
              }
            })
          )
        }
      })
    );
  }

  onDelete(activity: any): void {
    const data: ConfirmationDialogData = {
      title: 'Usuwanie aktywności',
      message: 'Czy na pewno chcesz usunąć tę aktywność?',
      acceptButton: 'Usuń'
    };

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, { data, width: '500px' });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          this.activityService.deleteUserActivity(activity.id).subscribe(() => {
            if (this.isToday()) {
              this.userActivities.set(this.userActivities().filter(a => a.id !== activity.id));
              this.activityService.emitFilterChange();
            } else {
              const findUserActivity = this.userActivities().find(a => a.id === activity.id)!;
              this.tableData.set(this.tableData().filter(a => a.id !== activity.id));
              this.userActivities.set(this.userActivities().filter(a => a.id !== activity.id));

              if (this.dateService.checkIfToday(findUserActivity.activityTime)) {
                this.activityService.emitFilterChange(true);
              }
            }
          });
        }
      })
    )
  }

  emitFilterChange(): void {
    this.activityService.emitFilterChange();
  }

  handlePageEvent(e: PageEvent): void {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.activityService.emitFilterChange();
  }

  getDateFromLabel() {
    const {from, to} = this.dateService.getDateRange(this.date().label);
    this.date.set({ label: this.date().label, from, to });

    this.activityService.emitFilterChange();
  }

  fillColumns(): void {
    this.userDrugsDisplayedColumns = [
      { title: 'Nazwa', displayedColumn: 'name' },
      { title: 'Data', displayedColumn: 'activityTime' },
      { title: 'Czas trwania', displayedColumn: 'duration' },
      { title: 'Średnie tętno', displayedColumn: 'averageHeartRate' },
      { title: 'Spalone kalorie', displayedColumn: 'caloriesBurned' }
    ]
  }

  getSortKey(option: SortOption): string {
    return `${option.sortBy}-${option.order}`;
  }

  sortOptionMapper(option: string): string {
    switch (option) {
      case 'activityTime': return 'Czas aktywności';
      case 'duration' : return 'Czas trwania';
      case 'averageHeartRate': return 'Średnie tętno';
      case 'caloriesBurned': return 'Spalone kalorie';
      default: return '';
    }
  }
}
