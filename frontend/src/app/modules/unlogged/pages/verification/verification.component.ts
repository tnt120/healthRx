import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { BreakpointObserver } from '@angular/cdk/layout';
import { map, Observable } from 'rxjs';
import { StepperOrientation } from '@angular/cdk/stepper';
import { FormErrorsService } from '../../../../core/services/form-errors/form-errors.service';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrl: './verification.component.scss'
})
export class VerificationComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);

  private readonly breakPointObserver = inject(BreakpointObserver);

  private readonly formErrorsService = inject(FormErrorsService);

  verficationToken = '';

  userPersonalDataForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    sex: new FormControl('', Validators.required),
    birthdate: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', Validators.required),
  });

  doctorPersonalDataForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    sex: new FormControl('', Validators.required),
    birthdate: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', Validators.required),
    specializations: new FormControl('', Validators.required),
    city: new FormControl('', Validators.required),
  });

  stepperOrientation: Observable<StepperOrientation>;

  formErrors = signal({
    userPersonalDataForm: {
      firstName: '',
      lastName: '',
      sex: '',
      birthdate: '',
      phoneNumber: ''
    }
  })

  constructor() {
    this.stepperOrientation = this.breakPointObserver
      .observe('(min-width: 768px)')
      .pipe(map(({matches}) => (matches ? 'horizontal' : 'vertical')));
  }

  ngOnInit(): void {
    this.verficationToken = this.route.snapshot.paramMap.get('token') as string;
  }

  updateErrorMessage(form: FormGroup, field: string, name: string) {
    const control = form.get(field as string)!;

    if (control) {
      this.updateFormErrors(field, this.formErrorsService.getErrorMessage(control, name));
    }
  }

  updateFormErrors(field: string, message: string) {
    this.formErrors.update(errors => ({
      ...errors,
      [field]: message
    }))
  }
}
