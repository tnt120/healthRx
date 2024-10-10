import { Component, inject, input, OnInit, output, SimpleChanges } from '@angular/core';
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

  editEmit = output<UserActivityResponse>();

  deleteEmit = output<UserActivityResponse>();

  duration: string = '';

  icon: string = '';

  ngOnInit(): void {
      this.icon = this.activityService.getIcon(this.userActivity().activity.name);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userActivity']) {
      this.duration = this.activityService.getActivityDuration(this.userActivity()!.duration);
    }
  }

  onEdit(): void {
    this.editEmit.emit(this.userActivity());
  }

  onDelete(): void {
    this.deleteEmit.emit(this.userActivity());
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }
}
