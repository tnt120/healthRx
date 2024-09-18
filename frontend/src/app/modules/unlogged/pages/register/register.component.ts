import { Subscription } from 'rxjs';
import { AuthService } from './../../../../core/services/auth/auth.service';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormErrorsService } from '../../../../core/services/form-errors/form-errors.service';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { RegisterRequest } from '../../../../core/models/auth/register-request.model';
import { ErrorCodesService } from '../../../../core/services/error-codes/error-codes.service';
import { CustomSnackbarService } from '../../../../core/services/custom-snackbar/custom-snackbar.service';

interface ErrorsTypes {
  email: string,
  password: string,
  confirmPassword: string,
  overall: string
}

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss', '../../styles/auth.styles.scss']
})
export class RegisterComponent implements OnInit, OnDestroy {
  private readonly formErrorsService = inject(FormErrorsService);

  private readonly router = inject(Router);

  private readonly authService = inject(AuthService);

  private readonly errorCodesService = inject(ErrorCodesService);

  private readonly customSnackbarService = inject(CustomSnackbarService);

  registerForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required]),
    confirmPassword: new FormControl('', [Validators.required]),
    isDoctor: new FormControl(false)
  });

  formErrors = signal<ErrorsTypes>({
    email: '',
    password: '',
    confirmPassword: '',
    overall: ''
  })

  hidePassword = true;

  hideConfirmPassword = true;

  subscription: Subscription | undefined;

  ngOnInit(): void {
    this.registerForm.get('password')?.valueChanges.subscribe(() => {
      this.registerForm.get('confirmPassword')?.updateValueAndValidity();
    })

    this.registerForm?.valueChanges.subscribe(() => {
      if (this.registerForm.valid) {
        this.updateFormErrors('overall', '');
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  updateErrorMessage(field: keyof ErrorsTypes, name: string) {
    const control = this.registerForm.get(field as string)!;

    if (control) {
      this.updateFormErrors(field, this.formErrorsService.getErrorMessage(control, name));
    }
  }

  toogleHide() {
    this.hidePassword = !this.hidePassword;
  }

  toogleHideConfirm() {
    this.hideConfirmPassword = !this.hideConfirmPassword
  }

  redirectTo(path: string) {
    this.router.navigate([path]);
  }

  register() {
    if (this.registerForm.valid) {

      const credentials: RegisterRequest = {
        email: this.registerForm.get('email')!.value!,
        password: this.registerForm.get('password')!.value!,
        confirmPassword: this.registerForm.get('confirmPassword')!.value!,
        isDoctor: this.registerForm.get('isDoctor')?.value ?? false
      };

      this.subscription = this.authService.register(credentials).subscribe({
        next: () => {
          this.router.navigate(['/login']);
          this.customSnackbarService.openCustomSnackbar({ title: 'Rejestracja', message: 'Rejestracja przebiegła pomyślnie. Na podany adres email został wysłany link aktywacyjny.', type: 'success', duration: 5000 });
        },
        error: (err) => {
          if ([303].includes(err.error.code)) {
            const message = this.errorCodesService.getErrorMessage(err.error.code);
            this.updateFormErrors('overall', message);
          } else {
            this.updateFormErrors('overall', 'Wystąpił nieznany błąd. Spróbuj ponownie za chwilę');
          }

          this.registerForm.reset();
        }
      })
    } else {
      this.formErrors.update(errors => ({
        ...errors,
        overall: 'Uzupełnij poprawnie wszystkie pola'
      }));
    }
  }

  updateFormErrors(field: keyof ErrorsTypes, message: string) {
    this.formErrors.update(errors => ({
      ...errors,
      [field]: message
    }))
  }
}
