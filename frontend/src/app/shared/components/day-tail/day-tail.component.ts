import { Component, input, Input, model, output } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';
import { Days } from '../../../core/enums/days.enum';

@Component({
  selector: 'app-day-tail',
  templateUrl: './day-tail.component.html',
  styleUrl: './day-tail.component.scss'
})
export class DayTailComponent {
  day = input.required<string>();
  isChecked = model<boolean>(false);

  toggleClick = output<boolean>();

  getDayString(day: string): string {
    switch (day) {
      case 'MONDAY':
        return Days.MONDAY;
      case 'TUESDAY':
        return Days.TUESDAY;
      case 'WEDNESDAY':
        return Days.WEDNESDAY;
      case 'THURSDAY':
        return Days.THURSDAY;
      case 'FRIDAY':
        return Days.FRIDAY;
      case 'SATURDAY':
        return Days.SATURDAY;
      case 'SUNDAY':
        return Days.SUNDAY;
      default:
        return '';
    }
  }
}
