import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { StatisticsServiceService } from '../../../../core/services/statistics/statistics-service.service';
import { DateRangeOptions, DateRangeType, DateService } from '../../../../core/services/date/date.service';
import { DatePipe } from '@angular/common';
import { Subscription } from 'rxjs';;
import { DrugStatisticsResponse } from '../../../../core/models/drug-statistics-model';
import { TableColumn } from '../../../../shared/components/table/table.component';
import { StatisticsRequest } from '../../../../core/models/statistics-request.model';
import { StatisticsType } from '../../../../core/enums/statistics-type.enum';

@Component({
  selector: 'app-drug-statistics',
  templateUrl: './drug-statistics.component.html',
  styleUrl: './drug-statistics.component.scss',
  providers: [DatePipe]
})
export class DrugStatisticsComponent implements OnInit, OnDestroy {
  private readonly statisticsService = inject(StatisticsServiceService);

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

  stats = signal<DrugStatisticsResponse[] | null>(null);

  tableData: any[] = [];

  columns: TableColumn[] = [];

  isStatsLoading$ = this.statisticsService.getLoadingStatsState(StatisticsType.DRUG);

  ngOnInit(): void {
    this.date.set({
      label: this.dateRangeOptions[0].value,
      from: this.dateService.getDateRange(this.dateRangeOptions[0].value).from,
      to: this.dateService.getDateRange(this.dateRangeOptions[0].value).to
    });

    this.fillColumns();
    this.getDrugsStats();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  getDrugsStats(): void {
    const req: StatisticsRequest = {
      startDate: this.datePipe.transform(this.date().from, 'yyyy-MM-dd')! + 'T00:00:00',
      endDate: this.datePipe.transform(this.date().to, 'yyyy-MM-dd')! + 'T23:59:59'
    }

    this.subscriptions.push(
      this.statisticsService.getDrugsStatistics(req).subscribe((res) => {
        this.stats.set(res);

        this.tableData = res
          .filter(stat => stat?.totalDaysTaken || stat?.totalDaysMissed || stat?.totalDaysPartiallyTaken)
          .map(stat => ({
            name: `${stat.drug.name} (${stat.drug.power})`,
            pharmaceuticalFormName: stat.drug.pharmaceuticalFormName,
            compliancePercentage: Math.round(stat.compliancePercentage * 100) / 100,
            firstLogDate: stat?.firstLogDate?.substring(0, 10),
            lastLogDate: stat?.lastLogDate?.substring(0, 10),
            daysTaken: `${stat.totalDaysTaken} (${stat.totalDaysPartiallyTaken})`,
            daysMissed: stat.totalDaysMissed,
            dosesTaken: stat.totalDosesTaken,
            dosesMissed: stat.totalDosesMissed,
            punctuallyPercentage: Math.round(stat.punctualityPercentage * 100) / 100,
            avgDelay: Math.round(stat.avgDelay),
          }));
      })
    );
  }

  fillColumns(): void {
    this.columns = [
      { title: 'Nazwa (moc)', displayedColumn: 'name' },
      { title: 'Forma farmaceutyczna', displayedColumn: 'pharmaceuticalFormName' },
      { title: 'Procent zażycia leku [%]', displayedColumn: 'compliancePercentage' },
      { title: 'Pierwsze zażycie', displayedColumn: 'firstLogDate' },
      { title: 'Ostatnie zażycie', displayedColumn: 'lastLogDate' },
      { title: 'Dni z zażytymi wszystkimi dawkami (częścią dawek)', displayedColumn: 'daysTaken' },
      { title: 'Dni bez zażycia', displayedColumn: 'daysMissed' },
      { title: 'Zażyte dawki', displayedColumn: 'dosesTaken' },
      { title: 'Niezażyte dawki', displayedColumn: 'dosesMissed' },
      { title: 'Procent punktualności [%]', displayedColumn: 'punctuallyPercentage' },
      { title: 'Średnie opóźnienie [min]', displayedColumn: 'avgDelay' },
    ];
  }

  getDateFromLabel() {
    const {from, to} = this.dateService.getDateRange(this.date().label);
    this.date.set({ label: this.date().label, from, to });

    this.getDrugsStats();
  }

  toggleChart(): void {
    this.chartState.set(!this.chartState());
  }
}
