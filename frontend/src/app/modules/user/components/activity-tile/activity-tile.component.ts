import { Component, inject, input, OnInit, output } from '@angular/core';
import { Activity } from '../../../../core/models/activity.model';
import { ActivityService } from '../../../../core/services/activity/activity.service';

@Component({
  selector: 'app-activity-tile',
  templateUrl: './activity-tile.component.html',
  styleUrl: './activity-tile.component.scss'
})
export class ActivityTileComponent implements OnInit {
  private readonly activityService = inject(ActivityService);

  activity = input.required<Activity>();

  clickEmit = output<Activity>();

  icon: string = '';

  ngOnInit(): void {
    this.icon = this.activityService.getIcon(this.activity().name);
  }

  onClick(): void {
    this.clickEmit.emit(this.activity());
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }
}
