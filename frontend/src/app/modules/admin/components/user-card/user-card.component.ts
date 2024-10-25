import { Component, input, OnInit, output, signal, SimpleChanges } from '@angular/core';
import { UserResponse } from '../../../../core/models/user/user-response.model';
import { Roles } from '../../../../core/enums/roles.enum';

@Component({
  selector: 'app-user-card',
  templateUrl: './user-card.component.html',
  styleUrl: './user-card.component.scss'
})
export class UserCardComponent implements OnInit {
  user = input.required<UserResponse>();

  providedUser = input.required<UserResponse>();

  profilePicture = signal<string>('../../../../../assets/images/user.png');

  changeRoleEnabled = signal<boolean>(false);

  actionsEnabled = signal<boolean>(true);

  changeRoleEmit = output<UserResponse>();

  deleteUserEmit = output<UserResponse>();

  ngOnInit(): void {
    if (this.providedUser()?.pictureUrl) {
      this.profilePicture.set('data:image/jpeg;base64 ,' + this.providedUser().pictureUrl);
    }

    this.changeRoleEnabled.set(this.isChangeRoleEnabled());
    this.actionsEnabled.set(this.isActionsEnabled());
  }

  protected changeRole(): void {
    this.changeRoleEmit.emit(this.providedUser());
  }

  protected deleteUser(): void {
    this.deleteUserEmit.emit(this.providedUser());
  }

  private isActionsEnabled(): boolean {
    if (this.providedUser().role === Roles.HEAD_ADMIN) return false;

    if (this.providedUser().role === Roles.ADMIN && this.user().role === Roles.ADMIN) return false;

    return true;
  }

  private isChangeRoleEnabled(): boolean {
    if (this.user().role === Roles.ADMIN && this.providedUser().role === Roles.ADMIN) return false;

    return !['DOCTOR', 'HEAD_ADMIN'].includes(this.providedUser().role)
  }

  protected getRoleString(): string {
    switch (this.providedUser().role) {
      case 'HEAD_ADMIN':
        return 'Główny administrator';
      case 'ADMIN':
        return 'Administrator';
      case 'DOCTOR':
        return 'Lekarz';
      case 'USER':
        return 'Użytkownik';
      default:
        return '';
    }
  }
}
