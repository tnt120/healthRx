import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { BreakpointObserver } from '@angular/cdk/layout';
import { catchError, EMPTY, map, Observable, tap, Subscription } from 'rxjs';
import { StepperOrientation } from '@angular/cdk/stepper';
import { FormErrorsService } from '../../../../core/services/form-errors/form-errors.service';
import { UserService } from '../../../../core/services/user/user.service';
import { ErrorCodesService } from '../../../../core/services/error-codes/error-codes.service';
import { VerificationDataResponse } from '../../../../core/models/user/verification-data-response.model';
import { Roles } from '../../../../core/enums/roles.enum';
import { UserVerificationRequest } from '../../../../core/models/user/user-verification-request.model';
import { UserSummary } from '../../models/user-summary-model';
import { Sex } from '../../../../core/enums/sex.enum';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  styleUrl: './verification.component.scss'
})
export class VerificationComponent implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);

  private readonly router = inject(Router);

  private readonly breakPointObserver = inject(BreakpointObserver);

  private readonly formErrorsService = inject(FormErrorsService);

  private readonly userService = inject(UserService);

  private readonly errorCodesService = inject(ErrorCodesService);

  personalDataForm = new FormGroup({
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    sex: new FormControl('', Validators.required),
    birthDate: new FormControl('', Validators.required),
    phoneNumber: new FormControl('', Validators.required),
  });

  userPersonalizationForm = new FormGroup({
    parameters: new FormGroup({}),
    height: new FormControl(null),
    isParametersNotifications: new FormControl(false),
    parametersNotificationsHour: new FormControl('18'),
    parametersNotificationsMinute: new FormControl('00'),
    isBadResultsNotificationsEnabled: new FormControl(false),
    isDrugNotificationsEnabled: new FormControl(false),
  });

  doctorPersonalizationForm = new FormGroup({
    specializations: new FormControl(null, Validators.required),
    city: new FormControl(null, Validators.required),
    numberPESEL: new FormControl('', Validators.required),
    numberPWZ: new FormControl('', Validators.required),
    idPhotoFrontUrl: new FormControl('front.jpg', Validators.required),
    idPhotoBackUrl: new FormControl('back.jpg', Validators.required),
    profilePictureUrl: new FormControl('profile.jpg', Validators.required),
  });

  verficationToken = '';

  errorMessage = signal<string>('');

  stepperOrientation: Observable<StepperOrientation>;

  verificationData$!: Observable<VerificationDataResponse>;

  data!: VerificationDataResponse;

  userData!: UserVerificationRequest;

  userSummary!: UserSummary;

  hours: string[] = [];

  minutes: string[] = ['00', '15', '30', '45'];

  stepperIndex = 0;

  subscription: Subscription | undefined;

  formErrors = signal<any>({
    personalDataForm: {
      firstName: '',
      lastName: '',
      sex: '',
      birthDate: '',
      phoneNumber: ''
    },
    doctorPersonalizationForm: {
      numberPESEL: '',
      numberPWZ: '',
    }
  })

  constructor() {
    this.stepperOrientation = this.breakPointObserver
      .observe('(min-width: 768px)')
      .pipe(map(({matches}) => (matches ? 'horizontal' : 'vertical')));
  }

  ngOnInit(): void {
    this.verficationToken = this.route.snapshot.paramMap.get('token') as string;

    this.verificationData$ = this.userService.getVerificationData(this.verficationToken)
      .pipe(
        tap(res => {
          this.data = res;
          if (res.role === Roles.USER) {
            res.parameters.forEach(parameter => {
              this.userPersonalizationForm.controls.parameters.addControl(parameter.id, new FormControl(false));
            });
            this.generateHours();
          }
        }),
        catchError(err => {
          if ([301, 305].includes(err.error.code)) {
            const message = this.errorCodesService.getErrorMessage(err.error.code);
            this.errorMessage.set(message);
          } else {
            this.errorMessage.set('Wystąpił nieznany błąd. Spróbuj ponownie za chwilę');
          }

          return EMPTY;
        })
      );
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  generateHours() {
    this.hours = Array.from({ length: 24 }, (v, k) => k < 10 ? '0' + k : k.toString());
  }

  clearHour() {
    this.userPersonalizationForm.controls.parametersNotificationsHour.setValue(this.hours[18]);
    this.userPersonalizationForm.controls.parametersNotificationsMinute.setValue(this.minutes[0]);
  }

  updateErrorMessage(form: FormGroup, formName: string, field: string, name: string) {
    const control = form.get(field as string)!;

    if (control) {
      this.updateFormErrors(formName, field, this.formErrorsService.getErrorMessage(control, name));
    }
  }

  updateFormErrors(formName: string, field: string, message: string) {
    this.formErrors.update(errors => ({
      ...errors,
      [formName]: {
        ...errors[formName],
        [field]: message
      }
    }));
  }

  prepareData() {
    const personalControls = this.personalDataForm.controls;
    const personalizationControls = this.userPersonalizationForm.controls;
    const doctorControls = this.doctorPersonalizationForm.controls;
    const parametersForm = this.userPersonalizationForm.get('parameters') as FormGroup;

    let data: UserVerificationRequest = {
      email: this.data.userEmail,
      firstName: personalControls.firstName.value!,
      lastName: personalControls.lastName.value!,
      sex: personalControls.sex.value!,
      birthDate: (personalControls.birthDate.value as unknown as Date).toISOString(),
      phoneNumber: personalControls.phoneNumber.value!
    }

    this.userSummary = {
      personalData: [
        { title: 'Imię', value: data.firstName },
        { title: 'Nazwisko', value: data.lastName },
        { title: 'Płeć', value: Sex[data.sex as keyof typeof Sex] },
        { title: 'Data urodzenia', value: data.birthDate.split('T')[0] },
        { title: 'Numer telefonu', value: data.phoneNumber },
      ]
    };

    if (this.data.role === Roles.DOCTOR && this.doctorPersonalizationForm.valid) {
      data = {...data,
        specializations: doctorControls.specializations.value!,
        city: doctorControls.city.value!,
        numberPESEL: doctorControls.numberPESEL.value!,
        numberPWZ: doctorControls.numberPWZ.value!,
        idPhotoFrontUrl: doctorControls.idPhotoFrontUrl.value!,
        idPhotoBackUrl: doctorControls.idPhotoBackUrl.value!,
        profilePictureUrl: doctorControls.profilePictureUrl.value!
      }

      this.userSummary = {...this.userSummary,
        doctorPersonal: [
          { title: 'Specjalizacje', value: data.specializations!.map(specialization => specialization.name).join(', ') },
          { title: 'Miasto', value: data.city!.name },
          { title: 'Numer PESEL', value: data.numberPESEL! },
          { title: 'Numer PWZ', value: data.numberPWZ! },
        ]
      };
    } else if (this.userPersonalizationForm.valid) {
      data = {...data,
        parameters: this.data.parameters.filter(parameter =>
            Object.keys(parametersForm.value).filter(key => parametersForm.value[key] === true).includes(parameter.id)
          ),
        parametersNotifications: personalizationControls.isParametersNotifications.value
          ? `${personalizationControls.parametersNotificationsHour.value}:${personalizationControls.parametersNotificationsMinute.value}`
          : null,
        isBadResultsNotificationsEnabled: personalizationControls.isBadResultsNotificationsEnabled.value!,
        isDrugNotificationsEnabled: personalizationControls.isDrugNotificationsEnabled.value!,
        ...(personalizationControls.height.value ? { height: personalizationControls.height.value } : {})
      }

      this.userSummary = {...this.userSummary,
        personalData: [...this.userSummary.personalData!,
          { title: 'Wzrost', value: data.height ? data.height + ' cm' : 'Brak' }
        ],
        userNotifications: [
          { title: 'Przypomnienie o wpisaniu parametrów', value: data.parametersNotifications ? 'Tak, o godzinie ' + data.parametersNotifications : 'Nie' },
          { title: 'Powiadomienie o złych wynikach', value: data.isBadResultsNotificationsEnabled ? 'Tak' : 'Nie' },
          { title: 'Powiadamianie o zażyciu leku wysokiego priorytetu', value: data.isDrugNotificationsEnabled ? 'Tak' : 'Nie' },
        ]
      }
    }


    this.userData = data;
  }

  verify() {
    this.subscription = this.userService.verifyUser(this.userData).subscribe({
      next: () => {
        alert('Konto zostało zweryfikowane. Możesz się zalogować');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        const message = this.errorCodesService.getErrorMessage(err.error.code);
        this.errorMessage.set(message);
      }
    })
  }
}
