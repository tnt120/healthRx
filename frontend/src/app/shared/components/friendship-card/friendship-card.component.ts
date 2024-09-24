import { Subscription } from 'rxjs';
import { Component, HostBinding, inject, input, model, OnDestroy, output } from '@angular/core';
import { FriendshipResponse } from '../../../core/models/friendship-response.model';
import { FriendshipService } from '../../../core/services/friendship/friendship.service';
import { MatDialog } from '@angular/material/dialog';
import { FriendshipPermissionUpdateDialogComponent } from '../friendship-permission-update-dialog/friendship-permission-update-dialog.component';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../confirmation-dialog/confirmation-dialog.component';

@Component({
  selector: 'app-friendship-card',
  templateUrl: './friendship-card.component.html',
  styleUrl: './friendship-card.component.scss'
})
export class FriendshipCardComponent implements OnDestroy {
  private readonly friendshipService = inject(FriendshipService);

  private readonly dialog = inject(MatDialog);

  @HostBinding('style.height.px') get height() {
    return this.friendship().status === 'ACCEPTED' ? 310 : 275;
  }

  friendship = input.required<FriendshipResponse>();
  isDoctor = input<boolean>(false);
  isRejected = model<boolean>(false);

  emitAcceptedAndDeleted = output<string>();
  emitResend = output<string>();

  subscriptions: Subscription[] = [];

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }

  approve() {
    this.subscriptions.push(
      this.friendshipService.acceptInvitation({ invitationId: this.friendship().friendshipId }).subscribe((res) => {
        this.emitAcceptedAndDeleted.emit(res.friendshipId);
      })
    )
  }

  reject() {
    this.subscriptions.push(
      this.friendshipService.rejectInvitation({ invitationId: this.friendship().friendshipId }).subscribe((res) => {
        this.emitAcceptedAndDeleted.emit(res.friendshipId);
      })
    )
  }

  resend() {
    this.subscriptions.push(
      this.friendshipService.resendInvitation({ invitationId: this.friendship().friendshipId }).subscribe((res) => {
        this.emitResend.emit(res.friendshipId);
        this.isRejected.set(false);
      })
    )
  }

  cancel() {
    const dialogData: ConfirmationDialogData = {
      title: 'Usuń znajomego',
      message: `Czy na pewno chcesz usunąć ${this.friendship().user.firstName} ${this.friendship().user.lastName} ze znajomych?`,
      acceptButton: 'Usuń'
    }

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, { data: dialogData });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          const isAccepted = this.friendship().status === 'ACCEPTED';
          this.subscriptions.push(
            this.friendshipService.cancelInvitation(this.friendship().friendshipId, isAccepted).subscribe((res) => {
              this.emitAcceptedAndDeleted.emit(res.friendshipId);
            })
          )
        }
      })
    )

  }

  editPermissions() {
    const dialogRef = this.dialog.open(FriendshipPermissionUpdateDialogComponent, { data: this.friendship().permissions });

    this.subscriptions.push(
      dialogRef.afterClosed().subscribe(res => {
        if (res) {
          this.subscriptions.push(
            this.friendshipService.updatePermissions(this.friendship().friendshipId, res).subscribe()
          )
        }
      })
    );
  }
}
