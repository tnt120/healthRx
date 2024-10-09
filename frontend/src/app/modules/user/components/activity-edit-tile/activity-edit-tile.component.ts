import { Component, inject, input, OnInit } from '@angular/core';
import { UserActivityResponse } from '../../../../core/models/user-activity-response.model';
import { ActivityService } from '../../../../core/services/activity/activity.service';

@Component({
  selector: 'app-activity-edit-tile',
  templateUrl: './activity-edit-tile.component.html',
  styleUrl: './activity-edit-tile.component.scss'
})
export class ActivityEditTileComponent implements OnInit {
 private readonly activityService = inject(ActivityService);

  userActivity = input.required<UserActivityResponse>();

  duration: string = '';

  icon: string = '';

  ngOnInit(): void {
      this.duration = this.activityService.getActivityDuration(this.userActivity()!.duration);
      this.icon = this.activityService.getIcon(this.userActivity().activity.name);
  }
}
