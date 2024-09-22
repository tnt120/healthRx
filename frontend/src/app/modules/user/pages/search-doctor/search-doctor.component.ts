import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { DoctorService } from '../../../../core/services/doctor/doctor.service';
import { Subscription } from 'rxjs';
import { Pagination } from '../../../../core/models/pagination.model';
import { SortOption } from '../../../../core/models/sort-option.model';
import { doctorSortOptions } from '../../../../core/constants/sort-options';
import { basePagination } from '../../../../core/constants/paginations-options';
import { PageEvent } from '@angular/material/paginator';
import { DoctorSearch } from '../../../../core/models/doctor-search.model';
import { FiltersAndSortDoctorSearch } from '../../components/search-doctors-filter-panel/search-doctors-filter-panel.component';

@Component({
  selector: 'app-search-doctor',
  templateUrl: './search-doctor.component.html',
  styleUrl: './search-doctor.component.scss'
})
export class SearchDoctorComponent implements OnInit, OnDestroy {
  private readonly doctorService = inject(DoctorService);

  doctors$ = this.doctorService.doctors$;

  isLoading$ = this.doctorService.getLoadingDoctorsState();

  subscriptions: Subscription[] = [];

  pagination: Pagination = {...basePagination};

  sortOptions: SortOption[] = [...doctorSortOptions];

  sort: SortOption = this.sortOptions[0];

  doctorSearch: DoctorSearch = {
    firstName: null,
    lastName: null,
    specialization: null,
    city: null
  };

  ngOnInit(): void {
    this.subscriptions.push(
      this.doctorService.getFilterChange().subscribe(() => this.getDoctors())
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getDoctors(): void {
    this.subscriptions.push(
      this.doctorService.getDoctors(this.pagination.pageIndex, this.pagination.pageSize, this.sort, this.doctorSearch).subscribe(res => {
        this.pagination.totalElements = res.totalElements;
      })
    )
  }

  handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.doctorService.emitFilterChange();
  }

  onSearch(e: FiltersAndSortDoctorSearch): void {
    this.doctorSearch = { ...e.filters };
    this.sort = { ...e.sort };
    this.doctorService.emitFilterChange();
  }
}
