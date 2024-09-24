import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FriendshipService } from '../../../../core/services/friendship/friendship.service';
import { Subscription } from 'rxjs';
import { basePagination } from '../../../../core/constants/paginations-options';
import { Pagination } from '../../../../core/models/pagination.model';
import { SortOption } from '../../../../core/models/sort-option.model';
import { friendshipSortOptions } from '../../../../core/constants/sort-options';
import { FriendshipSearch } from '../../../../core/models/friendship-search.model';
import { FiltersAndSortFriendshipSearch } from '../../../../shared/components/friendship-filter-panel/friendship-filter-panel.component';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-patients-dashboard',
  templateUrl: './patients-dashboard.component.html',
  styleUrl: './patients-dashboard.component.scss'
})
export class PatientsDashboardComponent implements OnInit, OnDestroy {
  private readonly friendshipService = inject(FriendshipService);

  subscriptions: Subscription[] = [];

  friendships$ = this.friendshipService.friendships$;

  isFriendshipsLoading$ = this.friendshipService.loadingFriendships$;

  pagination: Pagination = {...basePagination };

  sortOptions: SortOption[] = [...friendshipSortOptions];

  sort: SortOption = this.sortOptions[0];

  friendshipSerach: FriendshipSearch = {
    firstName: null,
    lastName: null,
  }

  ngOnInit(): void {
    this.subscriptions.push(
      this.friendshipService.getFilterChange().subscribe(() => this.getFriendships())
    )
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

  handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.friendshipService.emitFilterChange();
  }

  onDelete(friendshipId: string) {
    this.friendshipService.updateFriendships(friendshipId, 'delete');
  }
}
