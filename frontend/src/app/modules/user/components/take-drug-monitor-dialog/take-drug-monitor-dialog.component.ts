import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserDrugMonitorResponse } from '../../../../core/models/user-drug-monitor-response.model';

export interface TakeDrugMonitorDialogData {
  userDrug: UserDrugMonitorResponse;
}

@Component({
  selector: 'app-take-drug-monitor-dialog',
  templateUrl: './take-drug-monitor-dialog.component.html',
  styleUrl: './take-drug-monitor-dialog.component.scss'
})
export class TakeDrugMonitorDialogComponent implements OnInit {
  data: TakeDrugMonitorDialogData = inject(MAT_DIALOG_DATA);

  userDrug!: UserDrugMonitorResponse;

  hours: string[] = [];

  minutes: string[] = [];

  takenTime = {
    hours: '00',
    minutes: '00',
  }

  ngOnInit(): void {
    this.userDrug = {...this.data.userDrug};

    if (this.userDrug.takenTime) {
      const [hours, minutes, _] = this.userDrug.takenTime.split(':');
      this.takenTime = { hours, minutes };
    } else {
      const [hours, minutes, _] = this.userDrug.time.split(':');
      this.takenTime = { hours, minutes };
    }

    this.generateHoursAndMinutes();
    this.updateTakenTime();
  }

  updateTakenTime(): void {
    this.userDrug.takenTime = this.takenTime.hours + ':' + this.takenTime.minutes + ':00';
  }

  generateHoursAndMinutes() {
    this.hours = Array.from({ length: 24 }, (v, k) => k < 10 ? '0' + k : k.toString());
    this.minutes = Array.from({ length: 60 }, (v, k) => k < 10 ? '0' + k : k.toString());
  }

  isValid(): boolean {
    return !!(this.userDrug.takenTime && this.userDrug.takenTime.substring(0, 5) !== this.data.userDrug.takenTime?.substring(0, 5));
  }
}
