import { Component, inject, input, model, OnDestroy } from '@angular/core';
import { NotificationsData } from '../../../../core/models/notifications-data.model';
import { MatDialog } from '@angular/material/dialog';
import { NotificationsManageDialogComponent } from './notifications-manage-dialog/notifications-manage-dialog.component';
import { Subscription } from 'rxjs';
import { SettingsService } from '../../../../core/services/settings/settings.service';
import { Store } from '@ngrx/store';
import { notificationsSettingsActions } from '../../../../core/state/notifications-settings/notifications-settings.actions';

@Component({
  selector: 'app-notifications-manage',
  templateUrl: './notifications-manage.component.html',
  styleUrl: './notifications-manage.component.scss'
})
export class NotificationsManageComponent implements OnDestroy {
  private readonly dialog = inject(MatDialog);

  private readonly settingsService = inject(SettingsService);

  private readonly store = inject(Store);

  notificationsSettings = model.required<NotificationsData | null>();

  subscriptions: Subscription[] = [];

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  onEdit() {
    const dialogRef = this.dialog.open(NotificationsManageDialogComponent, { data: this.notificationsSettings(), minWidth: '90vw' });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          this.subscriptions.push(
            this.settingsService.notificationsChange(res).subscribe(() => {
              this.notificationsSettings.set(res);
              this.store.dispatch(notificationsSettingsActions.editSuccess({ notificationsSettings: res }));
            })
          )
        }
      })
    )
  }
}
