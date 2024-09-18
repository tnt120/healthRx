import { Component, inject, OnInit, signal } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NotificationsData } from '../../../../../core/models/notifications-data.model';

@Component({
  selector: 'app-notifications-manage-dialog',
  templateUrl: './notifications-manage-dialog.component.html',
  styleUrl: './notifications-manage-dialog.component.scss'
})
export class NotificationsManageDialogComponent implements OnInit {
  data: NotificationsData = inject(MAT_DIALOG_DATA);

  currValues = signal<NotificationsData & { minutes: string, hours: string }>({
    parametersNotifications: null,
    isBadResultsNotificationsEnabled: false,
    isDrugNotificationsEnabled: false,
    minutes: '18',
    hours: '00'
  });

  hours: string[] = [];

  minutes: string[] = ['00', '15', '30', '45'];

  ngOnInit(): void {
    this.currValues.set({
      parametersNotifications: this.data.parametersNotifications,
      isBadResultsNotificationsEnabled: this.data.isBadResultsNotificationsEnabled,
      isDrugNotificationsEnabled: this.data.isDrugNotificationsEnabled,
      hours: this.data.parametersNotifications ? this.data.parametersNotifications.split(':')[0] : '18',
      minutes: this.data.parametersNotifications ? this.data.parametersNotifications.split(':')[1] : '00',
    });

    this.generateHours();
  }

  private generateHours() {
    this.hours = Array.from({ length: 24 }, (v, k) => k < 10 ? '0' + k : k.toString());
  }

  isValid(): boolean {
    const parametersNotificationsCheck = this.data.parametersNotifications ?
      this.data.parametersNotifications !== `${this.currValues().hours}:${this.currValues().minutes}:00`
        || !this.currValues().parametersNotifications :
      !!this.currValues().parametersNotifications;

    return this.currValues().isBadResultsNotificationsEnabled !== this.data.isBadResultsNotificationsEnabled ||
      this.currValues().isDrugNotificationsEnabled !== this.data.isDrugNotificationsEnabled ||
      parametersNotificationsCheck;
  }

  getValue(): NotificationsData {
    return {
      parametersNotifications: this.currValues().parametersNotifications ? `${this.currValues().hours}:${this.currValues().minutes}:00` : null,
      isBadResultsNotificationsEnabled: this.currValues().isBadResultsNotificationsEnabled,
      isDrugNotificationsEnabled: this.currValues().isDrugNotificationsEnabled
    }
  }

}
