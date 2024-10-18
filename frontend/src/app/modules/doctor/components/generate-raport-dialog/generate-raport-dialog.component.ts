import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { GenerateReportRequest } from '../../../../core/models/generate-report-request.model';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../core/services/date/date.service';
import { DatePipe, NgIf } from '@angular/common';
import { FriendshipPermissions } from '../../../../core/models/friendship-response.model';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-generate-raport-dialog',
  standalone: true,
  imports: [MatDialogModule, MatInputModule, MatSelectModule, FormsModule, NgIf, MatCheckboxModule, MatButtonModule],
  templateUrl: './generate-raport-dialog.component.html',
  styleUrl: './generate-raport-dialog.component.scss',
  providers: [DatePipe]
})
export class GenerateRaportDialogComponent implements OnInit {
  private readonly dateService = inject(DateService);

  private readonly datePipe = inject(DatePipe);

  protected data: FriendshipPermissions = inject(MAT_DIALOG_DATA);

  result = signal<Omit<GenerateReportRequest, 'userId'>>({
    startDate: '',
    endDate: '',
    userDrugs: false,
    parametersStats: false,
    drugsStats: false,
    activitiesStats: false
  });

  dateRangeOptions = [...DateRangeOptions];

  dateLabel = signal<DateRangeType>(this.dateRangeOptions[0].value);

  ngOnInit(): void {
    this.setDateRange();
  }

  protected setDateRange(): void {
    const {from, to} = this.dateService.getDateRange(this.dateLabel());

    this.result.set({
      ...this.result(),
      startDate: this.datePipe.transform(from, 'yyyy-MM-dd')! + 'T00:00:00',
      endDate: this.datePipe.transform(to, 'yyyy-MM-dd')! + 'T23:59:59'
    });
  }

  protected isValid(): boolean {
    return this.result().userDrugs || this.result().parametersStats || this.result().drugsStats || this.result().activitiesStats;
  }
}
