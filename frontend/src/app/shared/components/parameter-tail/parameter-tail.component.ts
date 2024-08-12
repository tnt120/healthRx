import { Component, Input } from '@angular/core';
import { AbstractControl, FormControl } from '@angular/forms';
import { Parameter } from '../../../core/models/parameter.model';

@Component({
  selector: 'app-parameter-tail',
  templateUrl: './parameter-tail.component.html',
  styleUrl: './parameter-tail.component.scss'
})
export class ParameterTailComponent {
  @Input({ required: true })
  control!: AbstractControl | null;

  @Input({ required: true })
  parameter!: Parameter;

  getControl() {
    return this.control as FormControl;
  }
}
