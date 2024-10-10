import { Injectable } from '@angular/core';
import moment from 'moment';

export type DateRangeType =
  'today' |
  'currentWeek' |
  'previousWeek' |
  'currentMonth' |
  'previousMonth' |
  'last7Days' |
  'last30Days' |
  'last60Days' |
  'last90Days' |
  'last120Days' |
  'last180Days' |
  'last365Days' |
  'currentYear' |
  'previousYear';

export const DateRangeOptions: { label: string, value: DateRangeType }[] = [
  { label: 'Bieżący tydzień', value: 'currentWeek' },
  { label: 'Poprzedni tydzień', value: 'previousWeek' },
  { label: 'Bieżący miesiąc', value: 'currentMonth' },
  { label: 'Poprzedni miesiąc', value: 'previousMonth' },
  { label: 'Ostatnie 7 dni', value: 'last7Days' },
  { label: 'Ostatnie 30 dni', value: 'last30Days' },
  { label: 'Ostatnie 60 dni', value: 'last60Days' },
  { label: 'Ostatnie 90 dni', value: 'last90Days' },
  { label: 'Ostatnie 120 dni', value: 'last120Days' },
  { label: 'Ostatnie 180 dni', value: 'last180Days' },
  { label: 'Ostatnie 365 dni', value: 'last365Days' },
  { label: 'Bieżący rok', value: 'currentYear' },
  { label: 'Poprzedni rok', value: 'previousYear' },
  // { label: 'Niestandardowy', value: 'custom' },
]

moment.updateLocale('pl', {
  week: {
    dow: 1,
  },
})

@Injectable({
  providedIn: 'root'
})
export class DateService {
  private dateRangeCalculators = new Map<DateRangeType, () => { from: Date; to: Date }>([
    [
      'today',
      () => this.getLastDaysRange(0),
    ],
    [
      'currentWeek',
      () => this.getWeekRange(0),
    ],
    [
      'currentMonth',
      () => this.getMonthRange(0)
    ],
    [
      'previousWeek',
      () => this.getWeekRange(-1),
    ],
    [
      'previousMonth',
      () => this.getMonthRange(-1),
    ],
    [
      'last7Days',
      () => this.getLastDaysRange(7),
    ],
    [
      'last30Days',
      () => this.getLastDaysRange(30),
    ],
    [
      'last60Days',
      () => this.getLastDaysRange(60),
    ],
    [
      'last90Days',
      () => this.getLastDaysRange(90),
    ],
    [
      'last120Days',
      () => this.getLastDaysRange(120),
    ],
    [
      'last180Days',
      () => this.getLastDaysRange(180),
    ],
    [
      'last365Days',
      () => this.getLastDaysRange(365),
    ],
    [
      'currentYear',
      () => this.getYearRange(0),
    ],
    [
      'previousYear',
      () => this.getYearRange(-1),
    ],
  ]);


  private getWeekRange(weekOffset: number): { from: Date, to: Date } {
    const from = moment().add(weekOffset, 'weeks').startOf('week').toDate();
    const to = moment().add(weekOffset, 'weeks').endOf('week').toDate();
    return { from, to };
  }

  private getMonthRange(monthOffset: number): { from: Date, to: Date } {
    const from = moment().add(monthOffset, 'months').startOf('month').toDate();
    const to = moment().add(monthOffset, 'months').endOf('month').toDate();
    return { from, to };
  }

  private getYearRange(yearOffset: number): { from: Date, to: Date } {
    const from = moment().add(yearOffset, 'years').startOf('year').toDate();
    const to = moment().add(yearOffset, 'years').endOf('year').toDate();
    return { from, to };
  }

  private getLastDaysRange(days: number): { from: Date, to: Date } {
    const from = moment().subtract(days, 'days').startOf('day').toDate();
    const to = moment().endOf('day').toDate();
    return { from, to };
  }

  getDateRange(type: DateRangeType): { from: Date, to: Date } {
    return this.dateRangeCalculators.get(type)!();
  }

  checkIfToday(date: Date): boolean {
    const { from, } = this.getDateRange('today');

    return date.getFullYear() === from.getFullYear() &&
      date.getMonth() === from.getMonth() &&
      date.getDate() === from.getDate();
  }
}
