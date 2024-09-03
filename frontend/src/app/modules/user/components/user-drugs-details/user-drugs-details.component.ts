import { Component, input, model, output } from '@angular/core';
import { DrugResponse } from '../../../../core/models/drug-response.model';
import { animate, style, transition, trigger } from '@angular/animations';

export interface AddEditUserDrug {
    priority: string,
    days: string[],
    doseSize: number | null,
    times: { hours: string, minutes: string }[],
    dates: { from: Date | null, to: Date | null },
    amount: number | null,
}

@Component({
  selector: 'app-user-drugs-details',
  templateUrl: './user-drugs-details.component.html',
  styleUrl: './user-drugs-details.component.scss',
  animations: [
    trigger('animateExpand', [
      transition(':enter', [
        style({ opacity: 0, height: '0px', 'padding-top': '0px' }),
        animate('.3s ease', style({ opacity: 1, height: '*', 'padding-top': '*' }))
      ]),
      transition(':leave', [
        animate('.3s ease', style({ opacity: 0, height: '0px', 'padding-top': '0px' }))
      ])
    ])
  ]
})
export class UserDrugsDetailsComponent {
  selectedDrug = input.required<DrugResponse>();

  data = model<AddEditUserDrug>({
    priority: '',
    days: [],
    doseSize: null,
    dates: { from: null, to: null },
    times: [{ hours: '12', minutes: '00' }],
    amount: null,
  });

  cancelEmit = output();

  saveEmit = output<AddEditUserDrug>();

  hours: string[] = [];

  minutes: string[] = [];

  days: string[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  ngOnInit(): void {
    this.generateHoursAndMinutes();
  }

  generateHoursAndMinutes() {
    this.hours = Array.from({ length: 24 }, (v, k) => k < 10 ? '0' + k : k.toString());
    this.minutes = Array.from({ length: 60 }, (v, k) => k < 10 ? '0' + k : k.toString());
  }

  toggleDay(isChecked: boolean, day: string): void {
    if (!isChecked) {
      this.data.set({ ...this.data(), days: [...this.data().days].filter(d => d !== day) });
    } else {
      this.data.set({ ...this.data(), days: [...this.data().days, day] });
    }
  }

  updateTime(index: number): void {
    const times = [...this.data().times];
    times[index] = { hours: this.hours[index], minutes: this.minutes[index] };
  }

  addTime(): void {
    this.data.set({
      ...this.data(),
      times: [...this.data().times, { hours: '12', minutes: '00' }]
    });
  }

  removeTime(index: number): void {
    const times = [...this.data().times];
    if (times.length > 1) {
      times.splice(index, 1);
      this.data.set({ ...this.data(), times });
    }
  }

  checkValidity(): boolean {
    const { priority, days, doseSize, times, dates, amount } = this.data();

    const timesNotEqual = times.map(time => `${time.hours}:${time.minutes}`).some((time, i, arr) => arr.indexOf(time) !== i);

    if (!priority || days.length === 0 || !doseSize || doseSize <= 0 || !dates.from || timesNotEqual) {
      return false;
    }

    return this.validateDates();
  }

  validateDates(): boolean {
    const { from, to } = this.data().dates;

    if ((to && !from) || (from && to && new Date(from) > new Date(to))) {
      return false;
    }

    return true;
  }

  cancel(): void {
    this.cancelEmit.emit();
  }

  save(): void {
    if (this.checkValidity()) {
      this.saveEmit.emit(this.data());
    }
  }
}
