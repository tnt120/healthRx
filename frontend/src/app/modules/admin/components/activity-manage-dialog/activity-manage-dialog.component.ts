import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Activity } from '../../../../core/models/activity.model';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivityRequest } from '../../../../core/models/admin/activity-request.model';

export interface ActivityManageDialogData {
  activity?: Activity;
}

@Component({
  selector: 'app-activity-manage-dialog',
  templateUrl: './activity-manage-dialog.component.html',
  styleUrl: './activity-manage-dialog.component.scss'
})
export class ActivityManageDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);

  protected data: ActivityManageDialogData = inject(MAT_DIALOG_DATA);

  manageActivityForm = this.fb.group({
    name: this.fb.control<string>('', [ Validators.required ]),
    metFactor: this.fb.control<number | null>(null, [ Validators.required, Validators.min(0) ]),
    isPopular: this.fb.control<boolean>(false, [ Validators.required ]),
  });

  onStartValue = signal<ActivityRequest | null>(null);

  ngOnInit(): void {
    if (this.data.activity) {
      this.manageActivityForm.get('name')?.setValue(this.data.activity.name);
      this.manageActivityForm.get('metFactor')?.setValue(this.data.activity.metFactor);
      this.manageActivityForm.get('isPopular')?.setValue(this.data.activity.isPopular);

      this.onStartValue.set(this.manageActivityForm.value as ActivityRequest);
    }
  }

  protected isValid(): boolean {
    const isFormValid = this.manageActivityForm.valid;

    if (!isFormValid) return false;

    if (!this.data.activity) return isFormValid;

    return JSON.stringify(this.onStartValue()) !== JSON.stringify(this.manageActivityForm.value);
  }
}
