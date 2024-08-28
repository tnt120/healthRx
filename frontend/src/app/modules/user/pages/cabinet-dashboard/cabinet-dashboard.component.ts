import { Component, inject, OnInit } from '@angular/core';
import { Pagination } from '../../../../core/models/pagination.model';
import { basePagination } from '../../../../core/constants/paginations-options';
import { SortOption } from '../../../../core/models/sort-option.model';
import { drugSortOptions } from '../../../../core/constants/sort-options';
import { DrugsService } from '../../../../core/services/drugs/drugs.service';
import { Observable, tap } from 'rxjs';
import { PageResponse } from '../../../../core/models/page-response.model';
import { UserDrugsResponse } from '../../../../core/models/user-drugs-response.model';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { getDayName } from '../../../../core/enums/days.enum';
import { getPriorityName, Priority } from '../../../../core/enums/priority.enum';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-cabinet-dashboard',
  templateUrl: './cabinet-dashboard.component.html',
  styleUrl: './cabinet-dashboard.component.scss',
  providers: [DatePipe]
})
export class CabinetDashboardComponent implements OnInit {
  private readonly drugsService = inject(DrugsService);

  private readonly datePipe = inject(DatePipe);

  pagination: Pagination = {...basePagination};

  sortOptions: SortOption[] = [...drugSortOptions];

  sort: SortOption = this.sortOptions[0];

  tableData: any[] = [];

  userDrugsDisplayedColumns: TableColumn[] = [
    { title: 'Nazwa', displayedColumn: 'name' },
    { title: 'Forma farmaceutyczna', displayedColumn: 'pharmaceuticalFormName' },
    { title: 'Dni przyjmowania', displayedColumn: 'doseDays' },
    { title: 'Godziny przyjmowania', displayedColumn: 'doseTimes' },
    { title: 'Okres przyjmowania', displayedColumn: 'takingPeriod' },
    { title: 'Priorytet', displayedColumn: 'priority' },
    { title: 'Śledzenie zapasów', displayedColumn: 'tracking' },
  ];

  ngOnInit(): void {
    this.drugsService.getUserDrugs(this.pagination.pageIndex, this.pagination.pageSize, this.sort).subscribe(res => {
      this.tableData = res.content.map(userDrug => ({
        id: userDrug.id,
        name: userDrug.drug.name,
        pharmaceuticalFormName: userDrug.drug.pharmaceuticalFormName,
        doseDays: userDrug.doseDays.map(day => getDayName(day, true)).join(', '),
        doseTimes: userDrug.doseTimes.join(', '),
        takingPeriod: `${this.datePipe.transform(userDrug.startDate, 'dd/MM/YYYY')} - ${userDrug.endDate ? this.datePipe.transform(userDrug.endDate, 'dd/MM/YYYY') : ''}`,
        priority: getPriorityName(userDrug.priority),
        tracking: userDrug.amount ? `${userDrug.amount} ${userDrug.drug.unit}` : 'Nie'
      }));
      this.pagination.totalElements = res.totalElements;
      console.log(res, this.tableData, this.pagination);
    });
  }

  onEdit(userDrug: UserDrugsResponse): void {
    console.log(userDrug);
  }

  onDelete(userDrug: UserDrugsResponse): void {
    console.log(userDrug);
  }
}
