import { Component, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { StatisticsServiceService } from '../../../../../core/services/statistics/statistics-service.service';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../../core/services/date/date.service';
import { Store } from '@ngrx/store';
import { DatePipe } from '@angular/common';
import { ChartResponse } from '../../../../../core/models/chart-response.model';
import { Parameter } from '../../../../../core/models/parameter.model';
import { map, Observable, Subscription } from 'rxjs';
import { UserParameterResponse } from '../../../../../core/models/user-parameter-response.model';
import { ChartRequest } from '../../../../../core/models/chart-request.model';
import { StatisticsType } from '../../../../../core/enums/statistics-type.enum';

@Component({
  selector: 'app-parameter-chart',
  templateUrl: './parameter-chart.component.html',
  styleUrl: './parameter-chart.component.scss',
  providers: [DatePipe]
})
export class ParameterChartComponent implements OnInit, OnDestroy {
  private readonly statisticsService = inject(StatisticsServiceService);

  private readonly dateService = inject(DateService);

  private readonly store = inject(Store);

  private readonly datePipe = inject(DatePipe);

  userHeight = input.required<number>();

  isChartLoading$ = this.statisticsService.getLoadingChartState(StatisticsType.PARAMETER);

  chartData = signal<ChartResponse & { selectedParameter: Parameter } | null>(null);

  dateRangeOptions = [...DateRangeOptions];

  date = signal<{ label: DateRangeType, from: Date | null, to: Date | null }> ({
    label: this.dateRangeOptions[0].value,
    from: null,
    to: null
  });

  subscriptions: Subscription[] = [];

  parameters$: Observable<Parameter[]> = this.store.select('userParameters').pipe(
    map(userParam => userParam.map((param: UserParameterResponse) => param.parameter))
  );

  selectedParameter = signal<Parameter | null>(null);

  ngOnInit(): void {
    this.date.set({
      label: this.dateRangeOptions[0].value,
      from: this.dateService.getDateRange(this.dateRangeOptions[0].value).from,
      to: this.dateService.getDateRange(this.dateRangeOptions[0].value).to
    })
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  getParameterChartData() {
    const req: ChartRequest = {
      dataId: this.selectedParameter()?.id || '',
      type: 'PARAMETER',
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
          selectedParameter: this.selectedParameter()!
        });
      })
    );
  }

  applyParameter() {
    if (this.selectedParameter() !== null) {
      this.getParameterChartData();
    }
  }

  getDateFromLabel() {
    const {from, to} = this.dateService.getDateRange(this.date().label);

    this.date.set({ label: this.date().label, from, to });

    if (this.selectedParameter() !== null) {
      this.getParameterChartData();
    }
  }
}
