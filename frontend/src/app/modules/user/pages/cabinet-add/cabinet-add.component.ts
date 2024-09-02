import { FormErrorsService } from './../../../../core/services/form-errors/form-errors.service';
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
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Days, getDayName } from '../../../../core/enums/days.enum';

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
    dates: string,
    amount: number | null,
  }>({
    priority: '',
    days: [],
    doseSize: null,
    dates: '',
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

    console.log(this.data().days);
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
}
