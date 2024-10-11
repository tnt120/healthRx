import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { StatisticsServiceService } from '../../../../../core/services/statistics/statistics-service.service';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../../core/services/date/date.service';
import { DatePipe } from '@angular/common';
import { StatisticsType } from '../../../../../core/enums/statistics-type.enum';
import { Subscription } from 'rxjs';
import { ChartResponse } from '../../../../../core/models/chart-response.model';
import { ChartRequest } from '../../../../../core/models/chart-request.model';

@Component({
  selector: 'app-activity-chart',
  templateUrl: './activity-chart.component.html',
  styleUrl: './activity-chart.component.scss',
  providers: [DatePipe]
})
export class ActivityChartComponent implements OnInit, OnDestroy {
  private readonly statisticsService = inject(StatisticsServiceService);

  private readonly dateService = inject(DateService);

  private readonly datePipe = inject(DatePipe);

  typeMapper = (value: 'count' | 'hours'): string => {
    const map = {
        count: 'Ilość wykonanych aktywności',
        hours: 'Suma godzin'
    };
    return map[value];
  };

  isChartLoading$ = this.statisticsService.getLoadingChartState(StatisticsType.ACTIVITY);

  subscriptions: Subscription[] = [];

  chartData = signal<ChartResponse | null>(null);

  dataForChart = signal<ChartResponse | null>(null);

  dateRangeOptions = [...DateRangeOptions];

  date = signal<{ label: DateRangeType, from: Date | null, to: Date | null }> ({
    label: this.dateRangeOptions[0].value,
    from: null,
    to: null
  });

  type = signal<'count' | 'hours'>('count');

  ngOnInit(): void {
    this.date.set({
      label: this.dateRangeOptions[0].value,
      from: this.dateService.getDateRange(this.dateRangeOptions[0].value).from,
      to: this.dateService.getDateRange(this.dateRangeOptions[0].value).to
    });

    this.getActivityChartData();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  getActivityChartData(): void {
    const req: ChartRequest = {
      dataId: '',
      type: 'ACTIVITY',
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

        this.setDataForChart();
      })
    );
  }

  setDataForChart() {
    if (this.chartData() !== null) {
      this.dataForChart.set({
        name: this.chartData()!.name,
        startDate: this.chartData()!.startDate,
        endDate: this.chartData()!.endDate,
        data: this.chartData()!.data.map(item => ({
          label: item.label,
          value: this.type() === 'count' ? item.value : Math.round(item.additionalValue! / 60 * 10) / 10
        })),
      });
    } else {
      this.dataForChart.set(null);
    }
  }

  getDateFromLabel() {
    const {from, to} = this.dateService.getDateRange(this.date().label);

    this.date.set({ label: this.date().label, from, to });

    this.getActivityChartData();
  }

  changeType() {
    if (this.chartData() !== null) {
      this.setDataForChart();
    }
  }
}
