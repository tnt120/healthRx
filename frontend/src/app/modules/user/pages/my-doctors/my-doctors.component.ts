import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FriendshipService } from '../../../../core/services/friendship/friendship.service';
import { Subscription } from 'rxjs';
import { basePagination } from '../../../../core/constants/paginations-options';
import { Pagination } from '../../../../core/models/pagination.model';
import { PageEvent } from '@angular/material/paginator';
import { FriendshipSearch } from '../../../../core/models/friendship-search.model';
import { FriendshipResponse, FriendshipStatus } from '../../../../core/models/friendship-response.model';
import { FiltersAndSortFriendshipSearch } from '../../../../shared/components/friendship-filter-panel/friendship-filter-panel.component';
import { SortOption } from '../../../../core/models/sort-option.model';
import { friendshipSortOptions } from '../../../../core/constants/sort-options';

@Component({
  selector: 'app-my-doctors',
  templateUrl: './my-doctors.component.html',
  styleUrl: './my-doctors.component.scss'
})
export class MyDoctorsComponent implements OnInit, OnDestroy {
  private readonly friendshipService = inject(FriendshipService);

  subscriptions: Subscription[] = [];

  friendships$ = this.friendshipService.friendships$;

  isFriendshipsLoading$ = this.friendshipService.loadingFriendships$;

  isFriendshipsPendingLoading$ = this.friendshipService.loadingFriendshipsPending$;

  isFriendshipsRejectedLoading$ = this.friendshipService.loadingFriendshipsRejected$;

  friendshipsPending: FriendshipResponse[] = [];

  friendshipsRejected: FriendshipResponse[] = [];

  pagination: Pagination = {...basePagination};

  sortOptions: SortOption[] = [...friendshipSortOptions];

  sort: SortOption = this.sortOptions[0];

  friendshipSerach: FriendshipSearch = {
    firstName: null,
    lastName: null,
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.friendshipService.getFilterChange().subscribe(() => this.getFriendships())
    );

    this.getPendingsAndRejecteds();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  onSearch(e: FiltersAndSortFriendshipSearch): void {
    this.friendshipSerach = { ...e.filters };
    this.sort = { ...e.sort };
    this.friendshipService.emitFilterChange();
  }

  getFriendships() {
    this.subscriptions.push(
      this.friendshipService.getFriendships(this.pagination.pageIndex, this.sort, this.pagination.pageSize, this.friendshipSerach).subscribe(res => {
        this.pagination.totalElements = res.totalElements;
      })
    )
  }

  getPendingsAndRejecteds() {
    this.subscriptions.push(
      this.friendshipService.getFriendshipsPending().subscribe((friendships) => {
        this.friendshipsPending = [...friendships];
      })
    );

    this.subscriptions.push(
      this.friendshipService.getFriendshipsRejected().subscribe((friendships) => {
        this.friendshipsRejected = [...friendships];
      })
    );
  }

  handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.friendshipService.emitFilterChange();
  }

  onDelete(friendshipId: string, status: 'WAITING' | 'REJECTED' | 'ACCEPTED') {
    if (status === FriendshipStatus.WAITING) {
      this.friendshipsPending = this.friendshipsPending.filter((f) => f.friendshipId !== friendshipId);
    } else if (status === FriendshipStatus.REJECTED) {
        this.friendshipsRejected = this.friendshipsRejected.filter((f) => f.friendshipId !== friendshipId);
    } else {
      this.friendshipService.updateFriendships(friendshipId, 'delete');
    }
  }

  onResend(friendshipId: string) {
    const friendship = this.friendshipsRejected.find((f) => f.friendshipId === friendshipId);
    if (friendship) {
      this.friendshipsPending.push({ ...friendship, status: FriendshipStatus.WAITING });
      this.friendshipsRejected = this.friendshipsRejected.filter((f) => f.friendshipId !== friendshipId);
    }
  }
}
