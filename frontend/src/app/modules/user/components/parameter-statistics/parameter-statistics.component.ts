import { Component, inject, input, OnDestroy, OnInit, signal } from '@angular/core';
import { StatisticsServiceService } from '../../../../core/services/statistics/statistics-service.service';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../core/services/date/date.service';
import { DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';
import { StatisticsRequest } from '../../../../core/models/statistics-request.model';
import { ParameterStatisticsResponse } from '../../../../core/models/parameter-statistics-model';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { TrendType } from '../../../../core/enums/trend-type.enum';
import { StatisticsType } from '../../../../core/enums/statistics-type.enum';
import { ParametersService } from '../../../../core/services/parameters/parameters.service';

@Component({
  selector: 'app-parameter-statistics',
  templateUrl: './parameter-statistics.component.html',
  styleUrl: './parameter-statistics.component.scss',
  providers: [DatePipe]
})
export class ParameterStatisticsComponent implements OnInit, OnDestroy {
  private readonly statisticsService = inject(StatisticsServiceService);

  private readonly parametersService = inject(ParametersService);

  private readonly dateService = inject(DateService);

  private readonly datePipe = inject(DatePipe);

  userHeight = input.required<number>();

  dateRangeOptions = [...DateRangeOptions];

  date = signal<{ label: DateRangeType, from: Date | null, to: Date | null }> ({
    label: this.dateRangeOptions[0].value,
    from: null,
    to: null
  });

  subscriptions: Subscription[] = [];

  chartState = signal<boolean>(false);

  stats = signal<ParameterStatisticsResponse[] | null>(null);

  tableData: any[] = [];

  columns: TableColumn[] = [];

  isStatsLoading$ = this.statisticsService.getLoadingStatsState(StatisticsType.PARAMETER);

  ngOnInit(): void {
    this.date.set({
      label: this.dateRangeOptions[0].value,
      from: this.dateService.getDateRange(this.dateRangeOptions[0].value).from,
      to: this.dateService.getDateRange(this.dateRangeOptions[0].value).to
    });

    this.fillColums();
    this.getParamtersStats();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  getParamtersStats(): void {
    const req: StatisticsRequest = {
      startDate: this.datePipe.transform(this.date().from, 'yyyy-MM-dd')! + 'T00:00:00',
      endDate: this.datePipe.transform(this.date().to, 'yyyy-MM-dd')! + 'T23:59:59'
    };

    this.subscriptions.push(
      this.statisticsService.getParameterStatistics(req).subscribe((res) => {
        this.stats.set(res);

        this.tableData = res
          .filter(stat => stat.trend)
          .map(stat => ({
            name: `${stat.parameter.name} ${stat.parameter.name === 'Waga' ? `(BMI)` : ''} [${stat.parameter.unit}]`,
            standards: `${stat.parameter.minStandardValue} - ${stat.parameter.maxStandardValue}`,
            avg: `${Math.round(stat.avgValue * 100) / 100} ${stat.parameter.name === 'Waga' ? `(${this.parametersService.calculateBmi(this.userHeight(), stat.avgValue)})` : ''}`,
            min: `${stat.minValue} ${stat.parameter.name === 'Waga' ? `(${this.parametersService.calculateBmi(this.userHeight(), stat.minValue)})` : ''}`,
            max: `${stat.maxValue} ${stat.parameter.name === 'Waga' ? `(${this.parametersService.calculateBmi(this.userHeight(), stat.maxValue)})` : ''}`,
            outsideTheNormDays: `${stat.daysBelowMinValue + stat.daysAboveMaxValue} (${stat.daysBelowMinValue}/${stat.daysAboveMaxValue})`,
            firstLogDate: stat?.firstLogDate?.substring(0, 10),
            lastLogDate: stat?.lastLogDate?.substring(0, 10),
            daysWithoutLogs: `${stat.missedDays} (${stat.longestBreak})`,
            logsCount: stat.logsCount,
            iconTrend: {
              icon: this.getTrendIcon(TrendType[stat.trend as unknown as keyof typeof TrendType]),
              tooltip: TrendType[stat.trend as unknown as keyof typeof TrendType],
              color: this.getTrendColor(TrendType[stat.trend as unknown as keyof typeof TrendType])
            }
          }));
      })
    );
  }

  getTrendIcon(trend: TrendType): string {
    switch (trend) {
      case TrendType.VERY_INCREASING:
        return 'arrow_upward';
      case TrendType.SLIGHTLY_INCREASING:
        return 'trending_up';
      case TrendType.STABLE:
        return 'trending_flat';
      case TrendType.SLIGHTLY_DECREASING:
        return 'trending_down';
      case TrendType.VERY_DECREASING:
        return 'arrow_downward';
    }
  }

  getTrendColor(trend: TrendType): string {
    switch (trend) {
      case TrendType.VERY_INCREASING:
        return 'red';
      case TrendType.SLIGHTLY_INCREASING:
        return 'yellow';
      case TrendType.STABLE:
        return 'green';
      case TrendType.SLIGHTLY_DECREASING:
        return 'yellow';
      case TrendType.VERY_DECREASING:
        return 'red';
    }
  }

  fillColums(): void {
    this.columns = [
      { title: 'Parametr [jednostka]', displayedColumn: 'name' },
      { title: 'Norma (od - do)', displayedColumn: 'standards' },
      { title: `Średnia`, displayedColumn: 'avg' },
      { title: `Wartość minialna`, displayedColumn: 'min' },
      { title: `Wartość maksymalna`, displayedColumn: 'max' },
      { title: 'Liczba pomiarów', displayedColumn: 'logsCount' },
      { title: 'Dni poza normą (poniżej/powyzej)', displayedColumn: 'outsideTheNormDays' },
      { title: 'Pierwszy pomiar', displayedColumn: 'firstLogDate' },
      { title: 'Ostatni pomiar', displayedColumn: 'lastLogDate' },
      { title: 'Dni bez pomiarów (najdłuższa przerwa)', displayedColumn: 'daysWithoutLogs' },
      { title: 'Trend', displayedColumn: 'iconTrend' }
    ]
  }

  getDateFromLabel() {
    const {from, to} = this.dateService.getDateRange(this.date().label);
    this.date.set({ label: this.date().label, from, to });

    this.getParamtersStats();
  }

  toggleChart(): void {
    this.chartState.set(!this.chartState());
  }
}
