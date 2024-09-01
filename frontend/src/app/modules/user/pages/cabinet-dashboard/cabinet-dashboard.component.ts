import { Subscription } from 'rxjs';
import { on } from '@ngrx/store';
import { TakeDrugMonitorDialogComponent, TakeDrugMonitorDialogData } from './../../components/take-drug-monitor-dialog/take-drug-monitor-dialog.component';
import { Component, inject, OnInit } from '@angular/core';
import { Pagination } from '../../../../core/models/pagination.model';
import { basePagination } from '../../../../core/constants/paginations-options';
import { SortOption } from '../../../../core/models/sort-option.model';
import { drugSortOptions } from '../../../../core/constants/sort-options';
import { DrugsService } from '../../../../core/services/drugs/drugs.service';
import { UserDrugsResponse } from '../../../../core/models/user-drugs-response.model';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { getCurrentDay, getDayName } from '../../../../core/enums/days.enum';
import { getPriorityName } from '../../../../core/enums/priority.enum';
import { DatePipe } from '@angular/common';
import { PageEvent } from '@angular/material/paginator';
import { UserDrugMonitorResponse } from '../../../../core/models/user-drug-monitor-response.model';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { UserDrugMonitorRequest } from '../../../../core/models/user-drug-monitor-request.model';

interface userDrugMonitor {
  drugsToTake: UserDrugMonitorResponse[];
  drugsTaken: UserDrugMonitorResponse[];
}

@Component({
  selector: 'app-cabinet-dashboard',
  templateUrl: './cabinet-dashboard.component.html',
  styleUrl: './cabinet-dashboard.component.scss',
  providers: [DatePipe]
})
export class CabinetDashboardComponent implements OnInit {
  private readonly drugsService = inject(DrugsService);

  private readonly datePipe = inject(DatePipe);

  private readonly dialog = inject(MatDialog);

  isDrugsSearching$ = this.drugsService.getLoadingDrugsState();

  isMonitorSeraching$ = this.drugsService.getLoadingMonitorState();

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

  userDrugMonitor: userDrugMonitor = { drugsToTake: [], drugsTaken: [] };

  subscriptions: Subscription[] = [];

  ngOnInit(): void {
    this.drugsService.getFilterChange().subscribe(() => this.loadUserDrugs());
    this.loadUserDrugMonitor();
  }

  loadUserDrugs(): void {
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

  loadUserDrugMonitor(): void {
    this.drugsService.getUserDrugMonitor().subscribe(res => {
      this.userDrugMonitor.drugsToTake = res.filter(drug => !drug.takenTime);
      this.userDrugMonitor.drugsTaken = res.filter(drug => drug.takenTime);

      console.log(this.userDrugMonitor);
    });
  }

  handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.drugsService.emitFilterChange();
  }

  onEdit(userDrug: UserDrugsResponse): void {
    console.log(userDrug);
  }

  onDelete(userDrug: UserDrugsResponse): void {
    console.log(userDrug);
  }

  onSetDrugMonitor(userDrug: UserDrugMonitorResponse): void {
    const data: TakeDrugMonitorDialogData = { userDrug };

    const dialogRef: MatDialogRef<TakeDrugMonitorDialogComponent, UserDrugMonitorResponse> = this.dialog.open(TakeDrugMonitorDialogComponent, { data, width: '400px' });

    this.subscriptions.push(dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const request: UserDrugMonitorRequest = this.prepareMonitorRequest(result);

        this.drugsService.setMonitorParameter(request).subscribe(res => {
          this.userDrugMonitor.drugsToTake = this.userDrugMonitor.drugsToTake.filter(drug => !(drug.id === res.id && drug.time === res.time));
          this.userDrugMonitor.drugsTaken = [...this.userDrugMonitor.drugsTaken, res];
        })
      }
    }))
  }

  onEditDrugMonitor(userDrug: UserDrugMonitorResponse): void {
    const data: TakeDrugMonitorDialogData = { userDrug };

    const dialogRef: MatDialogRef<TakeDrugMonitorDialogComponent, UserDrugMonitorResponse> = this.dialog.open(TakeDrugMonitorDialogComponent, { data, width: '400px' });

    this.subscriptions.push(dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const request: UserDrugMonitorRequest = this.prepareMonitorRequest(result);

        this.drugsService.editMonitorParameter(request).subscribe(res => {
          this.userDrugMonitor.drugsTaken = this.userDrugMonitor.drugsTaken.map(userDrug => userDrug.id === res.id && userDrug.time === res.time ? res : userDrug);
        });
      }
    }));
  }

  prepareMonitorRequest(result: UserDrugMonitorResponse): UserDrugMonitorRequest {
    return {
      id: result.id,
      drugId: result.drug.id,
      day: getCurrentDay(),
      time: result.time,
      takenTime: result.takenTime,
    }
  }
}
