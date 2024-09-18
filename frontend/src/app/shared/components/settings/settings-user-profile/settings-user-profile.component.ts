import { Subscription } from 'rxjs';
import { UserProfileEditDialog, UserProfileEditDialogComponent } from './../user-profile-edit-dialog/user-profile-edit-dialog.component';
import { Component, inject, input, model, OnDestroy } from '@angular/core';
import { UserResponse } from '../../../../core/models/user/user-response.model';
import { Sex } from '../../../../core/enums/sex.enum';
import { MatDialog } from '@angular/material/dialog';
import { SettingsService } from '../../../../core/services/settings/settings.service';
import { PersonalDataChangeRequest } from '../../../../core/models/personal-data-change.model';
import { Store } from '@ngrx/store';
import { userActions } from '../../../../core/state/user/user.actions';

@Component({
  selector: 'app-settings-user-profile',
  templateUrl: './settings-user-profile.component.html',
  styleUrl: './settings-user-profile.component.scss'
})
export class SettingsUserProfileComponent implements OnDestroy {
  private readonly dialog = inject(MatDialog);

  private readonly settingsSercice = inject(SettingsService);

  private readonly store = inject(Store);

  userProfileData = model.required<UserResponse | null>();

  subscriptions: Subscription[] = [];

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }

  getSex(): string {
    switch (this.userProfileData()?.sex as string) {
      case 'MAN': return Sex.MAN;
      case 'WOMAN': return Sex.WOMAN;
      case 'NO_ANSWER': return Sex.NO_ANSWER;
      case "NONE": return Sex.NONE;
      default: return 'Brak';
    }
  }

  getHeight(): string {
    return this.userProfileData()?.height ? this.userProfileData()?.height + ' cm' : 'Brak'
  }

  onEdit(field: keyof UserResponse): void {
    const dialogData: UserProfileEditDialog = {
      profilParameter: field,
      value: this.userProfileData()?.[field] || ''
    };

    const dialogRef = this.dialog.open(UserProfileEditDialogComponent, { data: dialogData, width: '400px' });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          const request: PersonalDataChangeRequest = {
            [field]: result,
            isHeightChanged: field === 'height' ? true : undefined
          };

          this.subscriptions.push(
            this.settingsSercice.persinalDataChange(request).subscribe(() => {
              this.userProfileData.set({ ...this.userProfileData() as UserResponse, [field]: result});

              this.store.dispatch(userActions.editSuccess({ userData: this.userProfileData() as UserResponse }));
            })
          );
        }
      })
    )
  }
}
