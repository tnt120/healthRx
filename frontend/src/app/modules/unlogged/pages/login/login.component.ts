import { AuthService } from './../../../../core/services/auth/auth.service';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormErrorsService } from '../../../../core/services/form-errors/form-errors.service';
import { Router } from '@angular/router';
import { LoginRequest } from '../../../../core/models/auth/login-request.model';
import { Roles } from '../../../../core/enums/roles.enum';
import { ErrorCodesService } from '../../../../core/services/error-codes/error-codes.service';

interface ErrorsTypes {
  email: string,
  password: string,
  overall: string
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss', '../../styles/auth.styles.scss']
})
export class LoginComponent implements OnInit {
  private readonly formErrorsService = inject(FormErrorsService);

  private readonly router = inject(Router);

  private readonly authService = inject(AuthService);

  private readonly errorCodesService = inject(ErrorCodesService);

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  formErrors = signal<ErrorsTypes>({
    email: '',
    password: '',
    overall: ''
  })

  hidePassword = true;

  ngOnInit(): void {
    this.loginForm?.valueChanges.subscribe(() => {
      if (this.loginForm.valid) {
        this.updateFormErrors('overall', '');
      }
    });
  }

  updateErrorMessage(field: keyof ErrorsTypes, name: string) {
    const control = this.loginForm.get(field as string)!;

    if (control) {
      this.updateFormErrors(field, this.formErrorsService.getErrorMessage(control, name));
    }
  }

  login() {
    if (this.loginForm.valid) {
      const credentials: LoginRequest = {
        email: this.loginForm.get('email')!.value!,
        password: this.loginForm.get('password')!.value!
      };

      this.authService.login(credentials).subscribe({
        next: (res) => {
          switch (res.user.role) {
            case Roles.HEAD_ADMIN || Roles.ADMIN:
              console.log(res, 'Naviage to admin panel');
              break;
            case Roles.USER:
              console.log(res, 'Navigate to user panel');
              break;
            case Roles.DOCTOR:
              console.log(res, 'Navigate to doctor panel');
              break;
          }
        },
        error: (err) => {
          if ([302, 307].includes(err.error.code)) {
            const message = this.errorCodesService.getErrorMessage(err.error.code);
            this.updateFormErrors('overall', message);
          } else {
            this.updateFormErrors('overall', 'Wystąpił nieznany błąd. Spróbuj ponownie za chwilę');
          }

          this.loginForm.reset();
        }
      });

    } else {
      this.updateFormErrors('overall', 'Uzupełnij poprawnie wszystkie pola');
    }
  }

  toogleHide() {
    this.hidePassword = !this.hidePassword;
  }

  redirectTo(path: string) {
    this.router.navigate([path]);
  }

  updateFormErrors(field: keyof ErrorsTypes, message: string) {
    this.formErrors.update(errors => ({
      ...errors,
      [field]: message
    }))
  }
}
