import { Component, output, signal } from '@angular/core';
import { UserSearchFilters } from '../../../../core/models/admin/user-search-filters.model';
import { Roles } from '../../../../core/enums/roles.enum';

@Component({
  selector: 'app-users-search-bar',
  templateUrl: './users-search-bar.component.html',
  styleUrl: './users-search-bar.component.scss'
})
export class UsersSearchBarComponent {
  search = output<UserSearchFilters>();

  filters = signal<UserSearchFilters>({
    role: null,
    firstName: null,
    lastName: null,
  });

  roles = signal<{role: Roles, translation: string}[]>([
    {
      role: Roles.USER,
      translation: 'Użytkownik'
    },
    {
      role: Roles.DOCTOR,
      translation: 'Lekarz'
    },
    {
      role: Roles.ADMIN,
      translation: 'Administrator'
    },
    {
      role: Roles.HEAD_ADMIN,
      translation: 'Główny administrator'
    }
  ]);

  onSearch(): void {
    this.search.emit(this.filters());
  }
}
