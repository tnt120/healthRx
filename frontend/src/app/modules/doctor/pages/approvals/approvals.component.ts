import { Subscription } from 'rxjs';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FriendshipService } from '../../../../core/services/friendship/friendship.service';
import { FriendshipResponse } from '../../../../core/models/friendship-response.model';

@Component({
  selector: 'app-approvals',
  templateUrl: './approvals.component.html',
  styleUrl: './approvals.component.scss'
})
export class ApprovalsComponent implements OnInit, OnDestroy {
  private readonly friendshipService = inject(FriendshipService);

  subscriptions: Subscription[] = [];

  friendshipsPending: FriendshipResponse[] = [];

  isLoading$ = this.friendshipService.getLoadingFriendshipsState();

  ngOnInit(): void {
    this.subscriptions.push(
      this.friendshipService.getFriendshipsPending().subscribe((friendships) => {
        this.friendshipsPending = [...friendships];
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  deleteFromPeding(friendshipId: string) {
    this.friendshipsPending = this.friendshipsPending.filter((f) => f.friendshipId !== friendshipId);
  }
}
