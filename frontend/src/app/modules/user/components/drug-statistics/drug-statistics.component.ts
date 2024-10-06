import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { StatisticsServiceService } from '../../../../core/services/statistics/statistics-service.service';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../core/services/date/date.service';
import { DatePipe } from '@angular/common';
import { Observable, Subscription } from 'rxjs';
import { ChartResponse } from '../../../../core/models/chart-response.model';
import { DrugsService } from '../../../../core/services/drugs/drugs.service';
import { DrugResponse } from '../../../../core/models/drug-response.model';
import { ChartRequest } from '../../../../core/models/chart-request.model';

@Component({
  selector: 'app-drug-statistics',
  templateUrl: './drug-statistics.component.html',
  styleUrl: './drug-statistics.component.scss'
})
export class DrugStatisticsComponent implements OnInit, OnDestroy {
  private readonly statisticsService = inject(StatisticsServiceService);

  private readonly drugService = inject(DrugsService);

  private readonly dateService = inject(DateService);

  private readonly datePipe = inject(DatePipe);

  subscriptions: Subscription[] = [];

  chartData = signal<ChartResponse | null>(null);

  dateRangeOptions = [...DateRangeOptions];

  date = signal<{ label: DateRangeType, from: Date | null, to: Date | null }> ({
    label: this.dateRangeOptions[0].value,
    from: null,
    to: null
  });

  userDrugs$!: Observable<DrugResponse[]>;

  selectedDrug = signal<DrugResponse | null>(null);

  ngOnInit(): void {
    this.getDrugs();

    this.date.set({
      label: this.dateRangeOptions[0].value,
      from: this.dateService.getDateRange(this.dateRangeOptions[0].value).from,
      to: this.dateService.getDateRange(this.dateRangeOptions[0].value).to
    })
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  getDrugs(): void {
    this.userDrugs$ = this.drugService.getDrugsUser();
  }

  getDrugChartData(): void {
    const req: ChartRequest = {
      dataId: this.selectedDrug()?.id || '',
      type: 'DRUG',
      startDate: this.datePipe.transform(this.date().from, 'yyyy-MM-dd')! + 'T00:00:00',
      endDate: this.datePipe.transform(this.date().to, 'yyyy-MM-dd')! + 'T23:59:59'
    };

    this.chartData.set(null);

    this.subscriptions.push(
      this.statisticsService.getChartData(req).subscribe((res) => {
        this.chartData.set({
          name: res.name,
          startDate: res.startDate,
          endDate: res.endDate,
          data: res.data,
        });
      })
    );
  }

  getDateFromLabel() {
    const {from, to} = this.dateService.getDateRange(this.date().label);

    this.date.set({ label: this.date().label, from, to });

    if (this.selectedDrug() !== null) {
      this.getDrugChartData();
    }
  }
}
