import { Component, input, OnDestroy, OnInit, output } from '@angular/core';
import { Subscription } from 'rxjs';
import { Activity } from '../../../../core/models/activity.model';

@Component({
  selector: 'app-popular-activities',
  templateUrl: './popular-activities.component.html',
  styleUrl: './popular-activities.component.scss'
})
export class PopularActivitiesComponent {
  popularActivities = input.required<Activity[]>();

  allActivities = input.required<Activity[]>();

  addActivityEmit = output<Activity>();


  addUserActivity(activity: Activity): void {
    this.addActivityEmit.emit(activity);
  }
}
