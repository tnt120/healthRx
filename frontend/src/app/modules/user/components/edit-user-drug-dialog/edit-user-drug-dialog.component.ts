import { map } from 'rxjs';
import { Component, inject, OnInit, signal } from '@angular/core';
import { UserDrugsResponse } from '../../../../core/models/user-drugs-response.model';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AddEditUserDrug } from '../user-drugs-details/user-drugs-details.component';

export interface EditUserDrugDialogData {
  userDrug: UserDrugsResponse;
}

export interface EditUserDrug {
  priority?: string,
  days?: string[],
  doseSize?: number | null,
  times?: { hours: string, minutes: string }[],
  dates?: { from: Date | null, to: Date | null },
  amount?: number | null,
}


@Component({
  selector: 'app-edit-user-drug-dialog',
  templateUrl: './edit-user-drug-dialog.component.html',
  styleUrl: './edit-user-drug-dialog.component.scss'
})
export class EditUserDrugDialogComponent implements OnInit {
  dialogData: EditUserDrugDialogData = inject(MAT_DIALOG_DATA);

  data = signal<AddEditUserDrug>({
    priority: '',
    days: [],
    doseSize: null,
    dates: { from: null, to: null },
    times: [{ hours: '12', minutes: '00' }],
    amount: 0,
  });

  prevData!: AddEditUserDrug;

  ngOnInit(): void {
    this.data.set({
      priority: this.dialogData.userDrug.priority,
      days: this.dialogData.userDrug.doseDays,
      doseSize: this.dialogData.userDrug.doseSize,
      dates: { from: new Date(this.dialogData.userDrug.startDate), to: this.dialogData.userDrug.endDate ? new Date(this.dialogData.userDrug.endDate) : null },
      times: this.dialogData.userDrug.doseTimes.map(time => ({ hours: time.substring(0, 2), minutes: time.substring(3, 5) })),
      amount: this.dialogData.userDrug.amount,
    });

    this.prevData = {
      ...this.data(),
      dates: { ...this.data().dates },
      times: this.data().times.map(time => ({ ...time }))
    };
  }

  isValid(): boolean {
    return true;
  }

  changeData(data: AddEditUserDrug) {
    this.data.set(data);
  }

  compareValues(key: string, prevValue: any, currentValue: any): boolean {
    if (key === 'dates') {
      prevValue = {
        from: prevValue.from.toLocaleString().substring(0, 10),
        to: prevValue.to ? prevValue.to.toLocaleString().substring(0, 10) : null
      };

      currentValue = {
        from: currentValue.from ? currentValue.from.toLocaleString().substring(0, 10) : null,
        to: currentValue.to ? currentValue.to.toLocaleString().substring(0, 10) : null
      }

      return !(prevValue.from === currentValue.from && prevValue.to === currentValue.to);
    }

    if (key === 'times') {
      prevValue = prevValue.map((time: { hours: string; minutes: string; }) => `${time.hours}:${time.minutes}`);
      currentValue = currentValue.map((time: { hours: string; minutes: string; }) => `${time.hours}:${time.minutes}`);
    }

    if (Array.isArray(prevValue) && Array.isArray(currentValue)) {
      if (prevValue.length !== currentValue.length) return true;
      return !prevValue.sort().every((val, index) => val === currentValue.sort()[index]);
    }

    return prevValue !== currentValue;
  }

  checkValidity(): boolean {
    const { priority, days, doseSize, times, dates, amount } = this.data();

    const timesNotEqual = times.map(time => `${time.hours}:${time.minutes}`).some((time, i, arr) => arr.indexOf(time) !== i);

    if (!priority || days.length === 0 || !doseSize || doseSize <= 0 || !dates.from || timesNotEqual) {
      return false;
    }

    if (!this.validateDates()) return false;

    const isModified = Object.keys(this.prevData).some(key => {
      let prevValue = (this.prevData as { [key: string]: any })[key];
      let currentValue = (this.data() as { [key: string]: any })[key];

      return this.compareValues(key, prevValue, currentValue);
    });

    return isModified;
  }

  getModifiedValues(): EditUserDrug {
    const modifiedValues: EditUserDrug = {};

    Object.keys(this.prevData).forEach(key => {
      let prevValue = (this.prevData as { [key: string]: any })[key];
      let currentValue = (this.data() as { [key: string]: any })[key];

      if (key === 'amount') return modifiedValues[key as keyof EditUserDrug] = currentValue;

      if (this.compareValues(key, prevValue, currentValue)) {
        modifiedValues[key as keyof EditUserDrug] = currentValue;
      }
    });

    return modifiedValues;
  }

  validateDates(): boolean {
    const { from, to } = this.data().dates;

    if ((to && !from) || (from && to && new Date(from) > new Date(to))) {
      return false;
    }

    return true;
  }

  save() {
    if (this.checkValidity()) {
      return this.getModifiedValues();
    } else {
      return null;
    }
  }
}
