import { Component, inject, signal } from '@angular/core';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../core/services/date/date.service';
import { Subscription } from 'rxjs';
import { DatePipe } from '@angular/common';
import { StatisticsServiceService } from '../../../../core/services/statistics/statistics-service.service';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { ActivityStatisticsResponse } from '../../../../core/models/activity-statistics-response.model';
import { StatisticsType } from '../../../../core/enums/statistics-type.enum';
import { StatisticsRequest } from '../../../../core/models/statistics-request.model';
import { ActivityService } from '../../../../core/services/activity/activity.service';

@Component({
  selector: 'app-activity-statistics',
  templateUrl: './activity-statistics.component.html',
  styleUrl: './activity-statistics.component.scss',
  providers: [DatePipe]
})
export class ActivityStatisticsComponent {
  private readonly statisticsService = inject(StatisticsServiceService);

  private readonly activityService = inject(ActivityService);

  private readonly dateService = inject(DateService);

  private readonly datePipe = inject(DatePipe);

  subscriptions: Subscription[] = [];

  dateRangeOptions = [...DateRangeOptions];

  date = signal<{ label: DateRangeType, from: Date | null, to: Date | null }> ({
    label: this.dateRangeOptions[0].value,
    from: null,
    to: null
  });

  chartState = signal<boolean>(false);

  stats = signal<ActivityStatisticsResponse[] | null>(null);

  tableData: any[] = [];

  columns: TableColumn[] = [];

  isStatsLoading$ = this.statisticsService.getLoadingStatsState(StatisticsType.ACTIVITY);

  ngOnInit(): void {
    this.date.set({
      label: this.dateRangeOptions[0].value,
      from: this.dateService.getDateRange(this.dateRangeOptions[0].value).from,
      to: this.dateService.getDateRange(this.dateRangeOptions[0].value).to
    });

    this.fillColumns();
    this.getActivityStats();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  getActivityStats(): void {
    const req: StatisticsRequest = {
      startDate: this.datePipe.transform(this.date().from, 'yyyy-MM-dd')! + 'T00:00:00',
      endDate: this.datePipe.transform(this.date().to, 'yyyy-MM-dd')! + 'T23:59:59'
    }

    this.subscriptions.push(
      this.statisticsService.getAcitvityStatistics(req).subscribe((res) => {
        this.stats.set(res);

        this.tableData = res
          .map(stat => ({
            name: stat.activity.name,
            firstLogDate: stat?.firstLogDate?.substring(0, 10),
            lastLogDate: stat?.lastLogDate?.substring(0, 10),
            minDuration: stat.avgDuration ? this.activityService.getActivityDuration(stat.minDuration) : '-',
            maxDuration: stat.avgDuration ? this.activityService.getActivityDuration(stat.maxDuration) : '-',
            avgDuration: stat.avgDuration ? this.activityService.getActivityDuration(stat.avgDuration) : '-',
            heartRate: stat.avgHeartRate ? stat.maxHeartRate + ' / ' + stat.minHeartRate + ' / ' + stat.avgHeartRate : '- / - / -',
            caloriesBurned: stat.avgCaloriesBurned ? stat.maxCaloriesBurned + ' / ' + stat.minCaloriesBurned + ' / ' + stat.avgCaloriesBurned : '- / - / -',
            logsCount: stat.logsCount,
            hoursCount: Math.round(stat.hoursCount * 10)/ 10,
          }));
      })
    );
  }

  fillColumns(): void {
    this.columns = [
      { title: 'Aktywność', displayedColumn: 'name' },
      { title: 'Pierwsze wystąpienie', displayedColumn: 'firstLogDate' },
      { title: 'Ostatnie wystąpienie', displayedColumn: 'lastLogDate' },
      { title: 'Ilość wykonanych', displayedColumn: 'logsCount' },
      { title: 'Ilość godzin', displayedColumn: 'hoursCount' },
      { title: 'Najkrótszy czas', displayedColumn: 'minDuration' },
      { title: 'Najdłuższy czas', displayedColumn: 'maxDuration' },
      { title: 'Średni czas', displayedColumn: 'avgDuration' },
      { title: 'Tętno (max/min/śr)', displayedColumn: 'heartRate' },
      { title: 'Spalone kalorie (max/min/śr)', displayedColumn: 'caloriesBurned' },
    ];
  }

  getDateFromLabel() {
    const {from, to} = this.dateService.getDateRange(this.date().label);
    this.date.set({ label: this.date().label, from, to });

    this.getActivityStats();
  }

  toggleChart(): void {
    this.chartState.set(!this.chartState());
  }
}
