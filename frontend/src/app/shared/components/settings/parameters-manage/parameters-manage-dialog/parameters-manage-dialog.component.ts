import { Subscription } from 'rxjs';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Parameter } from '../../../../../core/models/parameter.model';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormControl, FormGroup } from '@angular/forms';

export interface ParametersManageDialogData {
  userParameters: Parameter[];
  parameters: Parameter[];
}

@Component({
  selector: 'app-parameters-manage-dialog',
  templateUrl: './parameters-manage-dialog.component.html',
  styleUrl: './parameters-manage-dialog.component.scss'
})
export class ParametersManageDialogComponent implements OnInit, OnDestroy {
  data: ParametersManageDialogData = inject(MAT_DIALOG_DATA);

  selectedParameters: Parameter[] = [];
  currentSelectedParameters: Parameter[] = [];

  currParametersForm = new FormGroup({});

  subscriptions: Subscription[] = [];

  ngOnInit(): void {
    this.selectedParameters = this.data.parameters.filter(p => this.data.userParameters.some(up => up.id === p.id));
    this.currentSelectedParameters = [ ...this.selectedParameters ];

    this.data.parameters.forEach(p => {
      this.currParametersForm.addControl(p.id, new FormControl(this.selectedParameters.includes(p)));
    });

    this.currParametersForm.valueChanges.subscribe((changes: { [key: string]: boolean }) => {
      this.currentSelectedParameters = [];

      Object.keys(changes).forEach(paramId => {
        const isSelected = changes[paramId];

        const param = this.data.parameters.find(p => p.id === paramId);

        if (param && isSelected) {
          this.currentSelectedParameters.push(param);
        }
      })
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  isValid(): boolean {
    return this.selectedParameters.length !== this.currentSelectedParameters.length ||
      this.selectedParameters.some(p => !this.currentSelectedParameters.some(cp => cp.id === p.id));
  }
}
