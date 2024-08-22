import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserParameterResponse } from '../../../../core/models/user-parameter-response.model';

export interface EditParameterMonitorDialogData {
  userParameter: UserParameterResponse;
}

@Component({
  selector: 'app-edit-parameter-monitor-dialog',
  templateUrl: './edit-parameter-monitor-dialog.component.html',
  styleUrl: './edit-parameter-monitor-dialog.component.scss'
})
export class EditParameterMonitorDialogComponent implements OnInit {
  data: EditParameterMonitorDialogData = inject(MAT_DIALOG_DATA);

  userParam!: UserParameterResponse;

  ngOnInit(): void {
    this.userParam = {...this.data.userParameter};
  }

  isValid(): boolean {
    return !!(this.userParam.value && this.userParam.value > 0 && this.data.userParameter.value !== this.userParam.value);
  }
}
