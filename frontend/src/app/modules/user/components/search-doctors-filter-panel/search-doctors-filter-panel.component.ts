import { Component, inject, output } from '@angular/core';
import { DoctorSearch, DoctorSearchData } from '../../../../core/models/doctor-search.model';
import { SortOption } from '../../../../core/models/sort-option.model';
import { doctorSortOptions } from '../../../../core/constants/sort-options';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { Specialization } from '../../../../core/models/specialization.model';
import { City } from '../../../../core/models/city.model';

export interface FiltersAndSortDoctorSearch {
  filters: DoctorSearch;
  sort: SortOption;
}

@Component({
  selector: 'app-search-doctors-filter-panel',
  templateUrl: './search-doctors-filter-panel.component.html',
  styleUrl: './search-doctors-filter-panel.component.scss'
})
export class SearchDoctorsFilterPanelComponent {
  private readonly store = inject(Store);

  specializations$: Observable<Specialization[]> = this.store.select('specializations');

  cities$: Observable<City[]> = this.store.select('cities');

  filtersAndSortChange = output<FiltersAndSortDoctorSearch>();

  filters: DoctorSearchData = {
    firstName: null,
    lastName: null,
    specialization: [],
    city: null
  };

  sortOptions: SortOption[] = [...doctorSortOptions];

  sort: SortOption = this.sortOptions[0];

  onSearch(): void {
    this.filtersAndSortChange.emit({ filters: this.prepareSeachData(), sort: this.sort });
  }

  sortChange(): void {
    this.filtersAndSortChange.emit({ filters: this.prepareSeachData(), sort: this.sort });
  }

  prepareSeachData(): DoctorSearch {
    return {
      firstName: this.filters.firstName,
      lastName: this.filters.lastName,
      specialization: this.filters.specialization.join(','),
      city: this.filters.city
    }
  }

  getKey(option: SortOption): string {
    return `${option.sortBy}-${option.order}`;
  }

  sortOptionMapper(option: string): string {
    switch (option) {
      case 'firstName': return 'Imię';
      case 'lastName': return 'Nazwisko';
      case 'specialization': return 'Specjalizacja';
      case 'city': return 'Miasto';
      default: return '';
    }
  }
}
