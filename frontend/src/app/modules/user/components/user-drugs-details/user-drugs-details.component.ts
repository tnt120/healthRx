import { Subscription } from 'rxjs';
import { Component, inject, input, model, OnDestroy, OnInit, output, signal } from '@angular/core';
import { DrugResponse } from '../../../../core/models/drug-response.model';
import { animate, style, transition, trigger } from '@angular/animations';
import { DrugPack } from '../../../../core/models/drug-pack.model';
import { DrugsService } from '../../../../core/services/drugs/drugs.service';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { EditDrugStockDialogComponent, EditDrugStockDialogData } from '../edit-drug-stock-dialog/edit-drug-stock-dialog.component';

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
export class UserDrugsDetailsComponent implements OnInit, OnDestroy {
  private readonly drugService = inject(DrugsService);

  private readonly dialog = inject(MatDialog);

  selectedDrug = input.required<DrugResponse>();

  drugPacks: DrugPack[] = [];

  isDialog = input<boolean>(false);

  data = model<AddEditUserDrug>({
    priority: '',
    days: [],
    doseSize: null,
    dates: { from: null, to: null },
    times: [{ hours: '12', minutes: '00' }],
    amount: null,
  });

  isTrackStock = signal<boolean>(false);

  dataChange = output<AddEditUserDrug>();

  cancelEmit = output();

  saveEmit = output<AddEditUserDrug>();

  hours: string[] = [];

  minutes: string[] = [];

  days: string[] = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

  subscriptions: Subscription[] = [];

  ngOnInit(): void {
    this.subscriptions.push(this.drugService.getDrugPacks(this.selectedDrug().id).subscribe(res => {
      this.drugPacks = res.drugPacks;
    }));

    if (this.data().amount !== null) this.isTrackStock.set(true);

    this.generateHoursAndMinutes();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  generateHoursAndMinutes() {
    this.hours = Array.from({ length: 24 }, (v, k) => k < 10 ? '0' + k : k.toString());
    this.minutes = Array.from({ length: 60 }, (v, k) => k < 10 ? '0' + k : k.toString());
  }

  setAmount() {
    if (this.isTrackStock()) {
      this.data.set({ ...this.data(), amount: 0});
    } else {
      this.data.set({ ...this.data(), amount: null });
    }

    this.dataChange.emit(this.data());
  }

  editStock(): void {
    const data: EditDrugStockDialogData = {
      amount: this.data().amount!,
      drugPacks: this.drugPacks,
      unit: this.selectedDrug().unit,
    };

    const dialogRef: MatDialogRef<EditDrugStockDialogComponent, number> = this.dialog.open(EditDrugStockDialogComponent, { data, width: '800px' });

    dialogRef.afterClosed().subscribe(result => {
      if (result || result === 0) {
        this.data.set({ ...this.data(), amount: result });
        this.dataChange.emit(this.data());
      }
    });
  }

  toggleDay(isChecked: boolean, day: string): void {
    if (!isChecked) {
      this.data.set({ ...this.data(), days: [...this.data().days].filter(d => d !== day) });
    } else {
      this.data.set({ ...this.data(), days: [...this.data().days, day] });
    }

    this.dataChange.emit(this.data());
  }

  updateTime(index: number): void {
    const times = [...this.data().times];
    times[index] = { hours: this.hours[index], minutes: this.minutes[index] };
    this.dataChange.emit(this.data());
  }

  addTime(): void {
    this.data.set({
      ...this.data(),
      times: [...this.data().times, { hours: '12', minutes: '00' }]
    });
    this.dataChange.emit(this.data());
  }

  removeTime(index: number): void {
    const times = [...this.data().times];
    if (times.length > 1) {
      times.splice(index, 1);
      this.data.set({ ...this.data(), times });
    }
    this.dataChange.emit(this.data());
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
