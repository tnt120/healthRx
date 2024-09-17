import { Component, input } from '@angular/core';
import { NotificationsData } from '../../../../core/models/notifications-data.model';

@Component({
  selector: 'app-notifications-manage',
  templateUrl: './notifications-manage.component.html',
  styleUrl: './notifications-manage.component.scss'
})
export class NotificationsManageComponent {
  notificationsSettings = input.required<NotificationsData | null>();
}
