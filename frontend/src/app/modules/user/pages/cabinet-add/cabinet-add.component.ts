import { Subscription } from 'rxjs';
import { CustomSnackbarService } from './../../../../core/services/custom-snackbar/custom-snackbar.service';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { DrugResponse } from '../../../../core/models/drug-response.model';
import { Pagination } from '../../../../core/models/pagination.model';
import { basePagination } from '../../../../core/constants/paginations-options';
import { drugSortOptions } from '../../../../core/constants/sort-options';
import { SortOption } from '../../../../core/models/sort-option.model';
import { DrugsService } from '../../../../core/services/drugs/drugs.service';
import { PageEvent } from '@angular/material/paginator';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { Router } from '@angular/router';
import { UserDrugsRequest } from '../../../../core/models/user-drugs-request.mode';
import { SnackBarData } from '../../../../core/models/snackbar-data.model';
import { AddEditUserDrug } from '../../components/user-drugs-details/user-drugs-details.component';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-cabinet-add',
  templateUrl: './cabinet-add.component.html',
  styleUrl: './cabinet-add.component.scss',
  providers: [DatePipe]
})
export class CabinetAddComponent implements OnInit, OnDestroy {
  private readonly drugsService = inject(DrugsService);

  private readonly router = inject(Router);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  private readonly datePipe = inject(DatePipe);

  isDrugsSearching$ = this.drugsService.getLoadingDrugsState();

  selectedDrug: DrugResponse | null = null;

  pagination: Pagination = {...basePagination};

  sortOptions: SortOption[] = [...drugSortOptions];

  sort: SortOption = this.sortOptions[0];

  tableData: any[] = [];

  searchString = '';

  userDrugsDisplayedColumns: TableColumn[] = [
    { title: 'Nazwa (moc)', displayedColumn: 'name' },
    { title: 'Forma farmaceutyczna', displayedColumn: 'pharmaceuticalFormName' },
  ];

  isFirstLoad = signal(true);

  subscriptions: Subscription[] = [];

  ngOnInit(): void {
    this.subscriptions.push(this.drugsService.getFilterChange().subscribe(() => this.loadDrugs()));
    this.isFirstLoad.set(false);
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  loadDrugs(): void {
    this.drugsService.getDrugs(this.pagination.pageIndex, this.pagination.pageSize, this.sort, this.searchString).subscribe(res => {
      this.tableData = res.content.map(drug => ({
        id: drug.id,
        name: `${drug.name} (${drug.power})`,
        pharmaceuticalFormName: drug.pharmaceuticalFormName,
        unit: drug.unit,
        atcCodes: drug.atcCodes,
      }));
      this.pagination.totalElements = res.totalElements;
    });
  }

  handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.drugsService.emitFilterChange();
  }

  onSearch(searchString: string): void {
    if (searchString === this.searchString) return;
    this.searchString = searchString;
    this.drugsService.emitFilterChange();
  }

  selectDrug(drug: DrugResponse | null) {
    this.selectedDrug = drug;
  }

  cancel(): void {
    this.router.navigate(['/user/cabinet']);
  }

  save(data: AddEditUserDrug): void {
    if (this.selectedDrug) {
      const request: UserDrugsRequest = {
        drugId: this.selectedDrug.id,
        doseSize: data.doseSize!,
        priority: data.priority,
        startDate: this.datePipe.transform(data.dates.from, 'yyyy-MM-dd')!,
        endDate: this.datePipe.transform(data.dates.to, 'yyyy-MM-dd') || null,
        amount: data.amount,
        doseTimes: data.times.map(time => `${time.hours}:${time.minutes}:00`),
        doseDays: data.days,
      };

      this.drugsService.addUserDrug(request).subscribe(() => {
        const snackBarData: SnackBarData = {
          title: 'Sukces',
          message: 'Lek został dodany do listy leków',
          type: 'success',
          duration: 3000
        };

        this.customSnackbarService.openCustomSnackbar(snackBarData);
        this.router.navigate(['/user/cabinet']);
      })
    }
  }
}
