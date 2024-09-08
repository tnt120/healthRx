import { Subscription } from 'rxjs';
import { TakeDrugMonitorDialogComponent, TakeDrugMonitorDialogData } from './../../components/take-drug-monitor-dialog/take-drug-monitor-dialog.component';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
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
import { Router } from '@angular/router';
import { CustomSnackbarService } from '../../../../core/services/custom-snackbar/custom-snackbar.service';
import { SnackBarData } from '../../../../core/models/snackbar-data.model';
import { ConfirmationDialogComponent, ConfirmationDialogData } from '../../../../shared/components/confirmation-dialog/confirmation-dialog.component';
import { EditUserDrug, EditUserDrugDialogComponent, EditUserDrugDialogData } from '../../components/edit-user-drug-dialog/edit-user-drug-dialog.component';
import { UserDrugsRequest } from '../../../../core/models/user-drugs-request.mode';

interface userDrugMonitor {
  drugsToTake: UserDrugMonitorResponse[];
  drugsTaken: UserDrugMonitorResponse[];
}

@Component({
  selector: 'app-cabinet-dashboard',
  templateUrl: './cabinet-dashboard.component.html',
  styleUrl: './cabinet-dashboard.component.scss',
  providers: [DatePipe],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CabinetDashboardComponent implements OnInit {
  private readonly cdRef = inject(ChangeDetectorRef);

  private readonly drugsService = inject(DrugsService);

  private readonly datePipe = inject(DatePipe);

  private readonly dialog = inject(MatDialog);

  private readonly router = inject(Router);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  isDrugsSearching$ = this.drugsService.getLoadingDrugsState();

  isMonitorSeraching$ = this.drugsService.getLoadingMonitorState();

  pagination: Pagination = {...basePagination};

  sortOptions: SortOption[] = [...drugSortOptions];

  sort: SortOption = this.sortOptions[0];

  tableData: any[] = [];

  userDrugs: UserDrugsResponse[] = [];

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

  nagivateToAdd() {
    this.router.navigate(['/user/cabinet/add']);
  }

  loadUserDrugs(): void {
    this.drugsService.getUserDrugs(this.pagination.pageIndex, this.pagination.pageSize, this.sort).subscribe(res => {
      this.tableData = res.content.map(userDrug => ({
        id: userDrug.id,
        name: userDrug.drug.name,
        pharmaceuticalFormName: userDrug.drug.pharmaceuticalFormName,
        doseDays: userDrug.doseDays.map(day => getDayName(day, true)).join(', '),
        doseTimes: userDrug.doseTimes.map(time => time.substring(0, 5)).join(', '),
        takingPeriod: `${this.datePipe.transform(userDrug.startDate, 'dd/MM/YYYY')} - ${userDrug.endDate ? this.datePipe.transform(userDrug.endDate, 'dd/MM/YYYY') : ''}`,
        priority: getPriorityName(userDrug.priority),
        tracking: userDrug.amount !== null ? `${userDrug.amount} ${userDrug.drug.unit}` : 'Nie'
      }));
      this.userDrugs = res.content;
      this.pagination.totalElements = res.totalElements;
    });
  }

  loadUserDrugMonitor(): void {
    this.drugsService.getUserDrugMonitor().subscribe(res => {
      this.userDrugMonitor.drugsToTake = res.filter(drug => !drug.takenTime);
      this.userDrugMonitor.drugsTaken = res.filter(drug => drug.takenTime);
      this.cdRef.detectChanges();
    });
  }

  handlePageEvent(e: PageEvent) {
    this.pagination.pageEvent = e;
    this.pagination.pageSize = e.pageSize;
    this.pagination.pageIndex = e.pageIndex;
    this.drugsService.emitFilterChange();
  }

  onEdit(userDrug: any): void {
    const data: EditUserDrugDialogData = { userDrug: this.userDrugs.find(drug => drug.id === userDrug.id)! };

    const dialogRef: MatDialogRef<EditUserDrugDialogComponent, EditUserDrug> = this.dialog.open(EditUserDrugDialogComponent, { data, minWidth: '90vw' });

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        let dateTo: string | null = null;
        if (!res.dates) {
          dateTo = data.userDrug.endDate ? data.userDrug.endDate : null;
        }

        const request: UserDrugsRequest = {
          drugId: data.userDrug.drug.id,
          doseSize: res.doseSize ? res.doseSize : undefined,
          priority: res.priority ? res.priority : undefined,
          startDate: res.dates?.from ? this.datePipe.transform(res.dates.from, 'yyyy-MM-dd')! : undefined,
          endDate: res.dates?.to ? this.datePipe.transform(res.dates.to, 'yyyy-MM-dd') : dateTo,
          amount: res.amount,
          doseTimes: res.times ? res.times.map(time => `${time.hours}:${time.minutes}:00`) : undefined,
          doseDays: res.days ? res.days : undefined,
        };

        this.drugsService.updateUserDrug(request, data.userDrug.id).subscribe(res => {
          const data: SnackBarData = { title: 'Sukces', message: 'Lek został zaktualizowany', type: 'success', duration: 3000 };
          this.customSnackbarService.openCustomSnackbar(data);
          this.userDrugs = this.userDrugs.map(ud => ud.id === res.id ? res : ud);
          this.tableData = this.tableData.map(ud => ud.id === res.id ? ({
            id: res.id,
            name: res.drug.name,
            pharmaceuticalFormName: res.drug.pharmaceuticalFormName,
            doseDays: res.doseDays.map(day => getDayName(day, true)).join(', '),
            doseTimes: res.doseTimes.map(time => time.substring(0, 5)).join(', '),
            takingPeriod: `${this.datePipe.transform(res.startDate, 'dd/MM/YYYY')} - ${res.endDate ? this.datePipe.transform(res.endDate, 'dd/MM/YYYY') : ''}`,
            priority: getPriorityName(res.priority),
            tracking: res.amount !== null ? `${res.amount} ${res.drug.unit}` : 'Nie'
          }) : ud);
          this.loadUserDrugMonitor();
        })
      }
    })
  }

  onDelete(userDrug: any): void {
    const dialogData: ConfirmationDialogData = {
      title: `Usunięcie leku`,
      message: `Czy na pewno chcesz usunąć lek "${userDrug.name}" ze swojej apteczki leków?`,
      acceptButton: 'Usuń',
    };

    const dialogRef = this.dialog.open(ConfirmationDialogComponent, { data: dialogData, width: '400px' });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.drugsService.deleteUserDrug(userDrug.id).subscribe(() => {
          const data: SnackBarData = { title: 'Sukces', message: 'Lek został usunięty z Twojej apteczki leków', type: 'success', duration: 3000 };
          this.customSnackbarService.openCustomSnackbar(data);
          this.drugsService.emitFilterChange();
          this.loadUserDrugMonitor();
        });
      }
    });
  }

  onSetDrugMonitor(userDrug: UserDrugMonitorResponse): void {
    const data: TakeDrugMonitorDialogData = { userDrug };

    const dialogRef: MatDialogRef<TakeDrugMonitorDialogComponent, UserDrugMonitorResponse> = this.dialog.open(TakeDrugMonitorDialogComponent, { data, width: '400px' });

    this.subscriptions.push(dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const request: UserDrugMonitorRequest = this.prepareMonitorRequest(result);

        this.drugsService.setMonitorDrug(request).subscribe(res => {
          this.userDrugMonitor.drugsToTake = this.userDrugMonitor.drugsToTake.filter(drug => !(drug.id === res.id && drug.time === res.time));
          this.userDrugMonitor.drugsTaken = [...this.userDrugMonitor.drugsTaken, res];

          this.updateTableAmount(res);

          this.cdRef.detectChanges();
        })
      }
    }))
  }

  onEditDrugMonitor(userDrug: UserDrugMonitorResponse): void {
    const data: TakeDrugMonitorDialogData = { userDrug };

    const dialogRef: MatDialogRef<TakeDrugMonitorDialogComponent, UserDrugMonitorResponse | { delete: boolean }> = this.dialog.open(TakeDrugMonitorDialogComponent, { data, width: '400px' });

    this.subscriptions.push(dialogRef.afterClosed().subscribe(result => {
      if (result) {
        if ('delete' in result) {
          if (!result.delete) return;

          this.drugsService.deleteMonitorDrug(userDrug.drug.id, userDrug.time).subscribe(() => {
            this.userDrugMonitor.drugsTaken = this.userDrugMonitor.drugsTaken.filter(drug => !(drug.id === userDrug.id && drug.time === userDrug.time));
            this.userDrugMonitor.drugsToTake = [...this.userDrugMonitor.drugsToTake, {...userDrug, takenTime: null}];
            this.updateTableAmount(userDrug, false);
            this.cdRef.detectChanges();
          });

        } else {
          const request: UserDrugMonitorRequest = this.prepareMonitorRequest(result);

          this.drugsService.editMonitorDrug(request).subscribe(res => {
            this.userDrugMonitor.drugsTaken = this.userDrugMonitor.drugsTaken.map(userDrug => userDrug.id === res.id && userDrug.time === res.time ? res : userDrug);
            this.cdRef.detectChanges();
          });
        }
      }
    }));
  }

  private updateTableAmount(userDrug: UserDrugMonitorResponse, isOdd: boolean = true): void {
    const findedUserDrug = this.userDrugs.find(ud => ud.id === userDrug.id)!;

    if (findedUserDrug.amount !== null) {
      let newAmount = 0;

      if (isOdd) {
        newAmount = findedUserDrug.amount - findedUserDrug.doseSize;
      } else {
        newAmount = findedUserDrug.amount + findedUserDrug.doseSize;
      }

      findedUserDrug.amount = newAmount < 0 ? 0 : newAmount;

      this.userDrugs = this.userDrugs.map(ud => ud.id === userDrug.id ? findedUserDrug : ud);

      this.tableData = this.tableData.map(ud => ud.id !== userDrug.id ? ud : ({
        ...ud,
        tracking: `${findedUserDrug.amount} ${findedUserDrug.drug.unit}`
      }));

    }
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
