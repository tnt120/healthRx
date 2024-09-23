import { Subscription } from 'rxjs';
import { Component, inject, input, model, OnDestroy, output } from '@angular/core';
import { FriendshipResponse } from '../../../core/models/friendship-response.model';
import { FriendshipService } from '../../../core/services/friendship/friendship.service';

@Component({
  selector: 'app-friendship-approval-card',
  templateUrl: './friendship-approval-card.component.html',
  styleUrl: './friendship-approval-card.component.scss'
})
export class FriendshipApprovalCardComponent implements OnDestroy {
  private readonly friendshipService = inject(FriendshipService);


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

  remove() {
    this.subscriptions.push(
      this.friendshipService.cancelInvitation(this.friendship().friendshipId).subscribe((res) => {
        this.emitAcceptedAndDeleted.emit(res.friendshipId);
      })
    )
  }
}
