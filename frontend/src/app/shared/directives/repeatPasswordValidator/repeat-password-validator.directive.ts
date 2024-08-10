import { Directive, input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator, ValidatorFn } from '@angular/forms';

export function confirmPasswordValidator(passwordField: string): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.parent) {
      return null;
    }

    const password = control.parent.get(passwordField)?.value;
    const confirmPassword = control.value;

    return password !== confirmPassword
      ? { passwordsDoNotMatch: true }
      : null;
  };
}

@Directive({
  selector: '[appRepeatPassword]',
  providers: [{ provide: NG_VALIDATORS, useExisting: RepeatPasswordValidatorDirective, multi: true }]

})
export class RepeatPasswordValidatorDirective implements Validator {

  appRepeatPassword = input.required<string>();

  validate(control: AbstractControl): ValidationErrors | null {
    return confirmPasswordValidator(this.appRepeatPassword())(control);
  }

}
