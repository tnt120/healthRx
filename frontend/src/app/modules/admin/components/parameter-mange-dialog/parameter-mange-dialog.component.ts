import { Parameter } from './../../../../core/models/parameter.model';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { Unit } from '../../../../core/models/unit.model';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ParameterRequest } from '../../../../core/models/admin/parameter-request.model';

export interface ParameterMangeDialogData {
  parameter?: Parameter;
  units: Unit[];
}

@Component({
  selector: 'app-parameter-mange-dialog',
  templateUrl: './parameter-mange-dialog.component.html',
  styleUrl: './parameter-mange-dialog.component.scss'
})
export class ParameterMangeDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);

  protected data: ParameterMangeDialogData = inject(MAT_DIALOG_DATA);

  manageParameterForm = this.fb.group({
    name: this.fb.control<string>('', [ Validators.required ]),
    hint: this.fb.control<string>('', [ Validators.required ]),
    unitId: this.fb.control<string>('', [ Validators.required ]),
    minStandardValue: this.fb.control<number | null>(null, [ Validators.required, Validators.min(0) ]),
    maxStandardValue: this.fb.control<number | null>(null, [ Validators.required, Validators.min(0) ]),
    minValue: this.fb.control<number | null>(null, [ Validators.required, Validators.min(0) ]),
    maxValue: this.fb.control<number | null>(null, [ Validators.required, Validators.min(0) ]),
  });

  onStartValue = signal<ParameterRequest | null>(null);

  ngOnInit(): void {
    if (this.data.parameter) {
      this.manageParameterForm.get('name')?.setValue(this.data.parameter.name);
      this.manageParameterForm.get('hint')?.setValue(this.data.parameter.hint);
      const unitId: string = this.data.units.find(unit => unit.symbol === this.data.parameter!.unit)!.id;
      this.manageParameterForm.get('unitId')?.setValue(unitId);
      this.manageParameterForm.get('minStandardValue')?.setValue(this.data.parameter.minStandardValue);
      this.manageParameterForm.get('maxStandardValue')?.setValue(this.data.parameter.maxStandardValue);
      this.manageParameterForm.get('minValue')?.setValue(this.data.parameter.minValue);
      this.manageParameterForm.get('maxValue')?.setValue(this.data.parameter.maxValue);

      this.onStartValue.set(this.manageParameterForm.value as ParameterRequest);
    }
  }

  protected isValid(): boolean {
    const isFormValid = this.manageParameterForm.valid;

    if (!isFormValid) return false;

    const valuesValid = this.manageParameterForm.get('minStandardValue')?.value! <= this.manageParameterForm.get('maxStandardValue')?.value! &&
      this.manageParameterForm.get('minValue')?.value! <= this.manageParameterForm.get('maxValue')?.value! &&
      this.manageParameterForm.get('minValue')?.value! <= this.manageParameterForm.get('minStandardValue')?.value! &&
      this.manageParameterForm.get('maxValue')?.value! >= this.manageParameterForm.get('maxStandardValue')?.value!;

    if (!this.data.parameter) return valuesValid;

    return valuesValid && JSON.stringify(this.manageParameterForm.value) !== JSON.stringify(this.onStartValue());
  }
}
