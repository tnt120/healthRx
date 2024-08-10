import { Directive, input } from '@angular/core';
import { AbstractControl, NG_VALIDATORS, ValidationErrors, Validator, ValidatorFn } from '@angular/forms';

interface ForbiddenRegex {
  password: string;
}

export function forbiddenNameValidator(type: keyof ForbiddenRegex): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    let regex: RegExp;

    switch (type) {
      case 'password':
        regex = /^(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{12,}$/;
        break;
    }

    const forbidden = regex.test(control.value);
    return !forbidden ? { [`forbidden${type.charAt(0).toUpperCase() + type.slice(1)}`]: { value: control.value } } : null;
  };
}

@Directive({
  selector: '[appForbiddenRegex]',
  providers: [{ provide: NG_VALIDATORS, useExisting: ForbiddenRegexValidatorDirective, multi: true }]
})
export class ForbiddenRegexValidatorDirective implements Validator {

  appForbiddenRegex = input.required<keyof ForbiddenRegex>();

  validate(control: AbstractControl): ValidationErrors | null {
    return this.appForbiddenRegex()
      ? forbiddenNameValidator(this.appForbiddenRegex())(control)
      : null;
  }
}
