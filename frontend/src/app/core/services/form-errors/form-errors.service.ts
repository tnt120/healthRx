import { Injectable } from '@angular/core';
import { AbstractControl, FormGroup } from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class FormErrorsService {
  getErrorMessage(control: AbstractControl, name: string): string {
    const errors = control?.errors;
    if (!errors) return '';

    const errorMessages: { [key: string]: string } = {
      required: `Pole ${name} jest wymagane`,
      email: `Nieprawidłowy email`,
      minlength: `Pole ${name} musi mieć co najmniej ${errors['minlength']?.requiredLength} znaków`,
      forbiddenPassword: `Pole ${name} musi posiadać co najmniej jedną: dużą literę, cyfrę i znak specjalny`,
      passwordsDoNotMatch: `Podane hasła nie są takie same`
    };

    for (const error in errors) {
      if (errors[error] && errorMessages[error]) {
        return errorMessages[error];
      }
    }

    return '';
  }
}
