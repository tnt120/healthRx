import { CustomSnackbarService } from './../../../../core/services/custom-snackbar/custom-snackbar.service';
import { Component, inject, OnInit, signal } from '@angular/core';
import { DrugResponse } from '../../../../core/models/drug-response.model';
import { Pagination } from '../../../../core/models/pagination.model';
import { basePagination } from '../../../../core/constants/paginations-options';
import { drugSortOptions } from '../../../../core/constants/sort-options';
import { SortOption } from '../../../../core/models/sort-option.model';
import { DrugsService } from '../../../../core/services/drugs/drugs.service';
import { PageEvent } from '@angular/material/paginator';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { animate, style, transition, trigger } from '@angular/animations';
import { Router } from '@angular/router';
import { UserDrugsRequest } from '../../../../core/models/user-drugs-request.mode';
import { SnackBarData } from '../../../../core/models/snackbar-data.model';

@Component({
  selector: 'app-cabinet-add',
  templateUrl: './cabinet-add.component.html',
  styleUrl: './cabinet-add.component.scss',
  animations: [
    trigger('animateExpand', [
      transition(':enter', [
        style({ opacity: 0, height: '0px', 'padding-top': '0px' }),
        animate('.3s ease', style({ opacity: 1, height: '*', 'padding-top': '*' }))
      ]),
      transition(':leave', [
        animate('.3s ease', style({ opacity: 0, height: '0px', 'padding-top': '0px' }))
      ])
    ])
  ]
})
export class CabinetAddComponent implements OnInit {
  private readonly drugsService = inject(DrugsService);

  private readonly router = inject(Router);

  private readonly customSnackbarService = inject(CustomSnackbarService);

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

  data= signal<{
    priority: string,
    days: string[],
    doseSize: number | null,
    times: { hours: string, minutes: string }[],
    dates: { from: Date | null, to: Date | null },
    amount: number | null,
  }>({
    priority: '',
    days: [],
    doseSize: null,
    dates: { from: null, to: null },
    times: [{ hours: '12', minutes: '00' }],
    amount: null,
  })

  hours: string[] = [];

  minutes: string[] = [];

  days: string[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  ngOnInit(): void {
    this.drugsService.getFilterChange().subscribe(() => this.loadDrugs());
    this.generateHoursAndMinutes();
    this.isFirstLoad.set(false);
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

  generateHoursAndMinutes() {
    this.hours = Array.from({ length: 24 }, (v, k) => k < 10 ? '0' + k : k.toString());
    this.minutes = Array.from({ length: 60 }, (v, k) => k < 10 ? '0' + k : k.toString());
  }

  onSearch(searchString: string): void {
    if (searchString === this.searchString) return;
    this.searchString = searchString;
    this.drugsService.emitFilterChange();
  }

  selectDrug(drug: DrugResponse | null) {
    this.selectedDrug = drug;
  }

  toggleDay(isChecked: boolean, day: string): void {
    if (!isChecked) {
      this.data.set({ ...this.data(), days: [...this.data().days].filter(d => d !== day) });
    } else {
      this.data.set({ ...this.data(), days: [...this.data().days, day] });
    }
  }

  updateTime(index: number): void {
    const times = [...this.data().times];
    times[index] = { hours: this.hours[index], minutes: this.minutes[index] };
  }

  addTime(): void {
    this.data.set({
      ...this.data(),
      times: [...this.data().times, { hours: '12', minutes: '00' }]
    });
  }

  removeTime(index: number): void {
    const times = [...this.data().times];
    if (times.length > 1) {
      times.splice(index, 1);
      this.data.set({ ...this.data(), times });
    }
  }

  checkValidity(): boolean {
    const { priority, days, doseSize, times, dates, amount } = this.data();

    const timesNotEqual = times.map(time => `${time.hours}:${time.minutes}`).some((time, i, arr) => arr.indexOf(time) !== i);

    if (!priority || days.length === 0 || !doseSize || doseSize <= 0 || !dates.from || timesNotEqual) {
      return false;
    }

    return this.validateDates();
  }

  validateDates(): boolean {
    const { from, to } = this.data().dates;

    if ((to && !from) || (from && to && new Date(from) > new Date(to))) {
      return false;
    }

    return true;
  }

  cancel(): void {
    this.router.navigate(['/user/cabinet']);
  }

  save(): void {
    if (this.checkValidity() && this.selectedDrug) {
      const request: UserDrugsRequest = {
        drugId: this.selectedDrug.id,
        doseSize: this.data().doseSize!,
        priority: this.data().priority,
        startDate: this.data().dates.from?.toISOString()!,
        endDate: this.data().dates.to?.toISOString() || null,
        amount: this.data().amount,
        doseTimes: this.data().times.map(time => `${time.hours}:${time.minutes}:00`),
        doseDays: this.data().days,
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
