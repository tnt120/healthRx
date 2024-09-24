import { Component, output, signal } from '@angular/core';
import { FriendshipSearch } from '../../../core/models/friendship-search.model';
import { SortOption } from '../../../core/models/sort-option.model';
import { friendshipSortOptions } from '../../../core/constants/sort-options';

export interface FiltersAndSortFriendshipSearch {
  filters: FriendshipSearch,
  sort: SortOption
}

@Component({
  selector: 'app-friendship-filter-panel',
  templateUrl: './friendship-filter-panel.component.html',
  styleUrl: './friendship-filter-panel.component.scss'
})
export class FriendshipFilterPanelComponent {
  filtersAndSortChange = output<FiltersAndSortFriendshipSearch>();

  filters = signal<FriendshipSearch>({
    firstName: null,
    lastName: null,
  });

  sortOptions: SortOption[] = [...friendshipSortOptions];

  sort: SortOption = this.sortOptions[0];

  onSearch(): void {
    this.filtersAndSortChange.emit({ filters: this.filters(), sort: this.sort });
  }

  sortChange(): void {
    this.filtersAndSortChange.emit({ filters: this.filters(), sort: this.sort });
  }

  getKey(option: SortOption): string {
    return `${option.sortBy}-${option.order}`;
  }

  sortOptionMapper(option: string): string {
    switch (option) {
      case 'createdAt': return 'Data dodania';
      default: return '';
    }
  }
}
