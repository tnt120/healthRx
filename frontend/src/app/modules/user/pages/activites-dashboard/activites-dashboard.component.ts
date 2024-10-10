import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Activity } from '../../../../core/models/activity.model';
import { ManageUserActivityData, ManageUserActivityDialogComponent } from '../../components/manage-user-activity-dialog/manage-user-activity-dialog.component';
import { Store } from '@ngrx/store';
import { ActivityService } from '../../../../core/services/activity/activity.service';
import { MatDialog } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { UserActivityRequest } from '../../../../core/models/user-activity-request.model';
import { DateService } from '../../../../core/services/date/date.service';

@Component({
  selector: 'app-activites-dashboard',
  templateUrl: './activites-dashboard.component.html',
  styleUrl: './activites-dashboard.component.scss'
})
export class ActivitesDashboardComponent implements OnInit, OnDestroy {
  private readonly store = inject(Store);

  private readonly activityService = inject(ActivityService);

  private readonly dialog = inject(MatDialog);

  private readonly dateService = inject(DateService);

  subscriptions: Subscription[] = [];

  popularActivities: Activity[] = [];

  allActivities: Activity[] = [];

  ngOnInit(): void {
    this.subscriptions.push(
      this.store.select('activities').subscribe(res => {
        this.popularActivities = res.mostPopularActivities;
        this.allActivities = [...res.mostPopularActivities, ...res.otherActivities];
      })
    )
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  addUserActivity(activity?: Activity): void {
    const data: ManageUserActivityData = {
      activity : activity || null,
      userActivity: null,
      activities: this.allActivities
    }

    const dialogRef = this.dialog.open(ManageUserActivityDialogComponent, { data, minWidth: '50vw' });

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

          this.activityService.addUserActivity(req).subscribe((addedActivity) => {
            if (this.dateService.checkIfToday(addedActivity.activityTime)) {
              this.activityService.emitFilterChange(true);
            }
            this.activityService.emitFilterChange(false);
          });
        }
      })
    )
  }
}
