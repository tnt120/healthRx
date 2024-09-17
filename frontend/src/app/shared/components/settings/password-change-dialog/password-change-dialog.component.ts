import { Subscription } from 'rxjs';
import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FormErrorsService } from '../../../../core/services/form-errors/form-errors.service';
import { SettingsService } from '../../../../core/services/settings/settings.service';
import { PasswordChangeRequest } from '../../../../core/models/password-change-request.model';
import { ErrorCodesService } from '../../../../core/services/error-codes/error-codes.service';

interface ErrorsTypes {
  overall: string,
  currentPassword: string,
  newPassword: string,
  newPasswordConfirmation: string,
}

@Component({
  selector: 'app-password-change-dialog',
  templateUrl: './password-change-dialog.component.html',
  styleUrl: './password-change-dialog.component.scss'
})
export class PasswordChangeDialogComponent implements OnInit, OnDestroy {
  private readonly formErrorsService = inject(FormErrorsService);

  private readonly settingsService = inject(SettingsService);

  private readonly errorCodesService = inject(ErrorCodesService);

  subscriptions: Subscription[] = [];

  passwordChangeForm = new FormGroup({
    currentPassword: new FormControl('', [Validators.required]),
    newPassword: new FormControl('', [Validators.required]),
    newPasswordConfirmation: new FormControl('', [Validators.required]),
  });

  formErrors = signal<ErrorsTypes>({
    overall: '',
    currentPassword: '',
    newPassword: '',
    newPasswordConfirmation: '',
  });

  hideCurrentPassword = true;

  hideNewPassword = true;

  hideNewPasswordConfirmation = true;

  ngOnInit(): void {
    this.passwordChangeForm.get('newPassword')?.valueChanges.subscribe(() => {
      this.passwordChangeForm.get('newPasswordConfirmation')?.updateValueAndValidity();
    })

    this.passwordChangeForm?.valueChanges.subscribe(() => {
      if (this.passwordChangeForm.valid) {
        this.updateFormErrors('overall', '');
      }
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  toogleHideCurrent() {
    this.hideCurrentPassword = !this.hideCurrentPassword;
  }

  toogleHideNew() {
    this.hideNewPassword = !this.hideNewPassword
  }

  toggleHideNewConfirmation() {
    this.hideNewPasswordConfirmation = !this.hideNewPasswordConfirmation;
  }

  updateErrorMessage(field: keyof ErrorsTypes, name: string) {
    const control = this.passwordChangeForm.get(field as string)!;

    if (control) {
      this.updateFormErrors(field, this.formErrorsService.getErrorMessage(control, name));
    }
  }

  updateFormErrors(field: keyof ErrorsTypes, message: string) {
    this.formErrors.update(errors => ({
      ...errors,
      [field]: message
    }))
  }

  changePassword() {
    if (this.passwordChangeForm.valid) {
      const request: PasswordChangeRequest = {
        currentPassword: this.passwordChangeForm.get('currentPassword')!.value!,
        newPassword: this.passwordChangeForm.get('newPassword')!.value!,
        newPasswordConfirmation: this.passwordChangeForm.get('newPasswordConfirmation')!.value!,
      };

      this.subscriptions.push(this.settingsService.passwordChange(request).subscribe({
        next: () => {
          this.passwordChangeForm.reset();
        },
        error: (err) => {
          this.passwordChangeForm.reset();

          if ([321, 322, 323].includes(err.error.code)) {
            const message = this.errorCodesService.getErrorMessage(err.error.code);
            this.updateFormErrors('overall', message);
          } else {
            this.updateFormErrors('overall', 'Wystąpił nieznany błąd. Spróbuj ponownie za chwilę');
          }
        }
      }))

    } else {
      this.updateErrorMessage('overall', 'formIsNotValid');
    }
  }
}
