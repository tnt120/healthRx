import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface UserProfileEditDialog {
  profilParameter: string;
  value: string | number;
}

@Component({
  selector: 'app-user-profile-edit-dialog',
  templateUrl: './user-profile-edit-dialog.component.html',
  styleUrl: './user-profile-edit-dialog.component.scss'
})
export class UserProfileEditDialogComponent implements OnInit {
  data: UserProfileEditDialog = inject(MAT_DIALOG_DATA);

  currValue!: string | number | Date;

  ngOnInit() {
    this.currValue = this.data.value;

    if (this.data.profilParameter === 'birthDate') {
      this.currValue = new Date(this.data.value as string);
      this.currValue.setHours(0, 0, 0, 0);
    }
  }

  isValid(): boolean {
    const basicTest = this.data.value !== this.currValue && this.currValue !== '' && this.currValue !== null

    if (this.data.profilParameter === 'phoneNumber') {
      return basicTest && /^(?:\d{9}|\+\d{11})$/.test(this.currValue as string);
    }

    if (this.data.profilParameter === 'birthDate') {
      return this.data.value !== this.formatDate(this.currValue.toLocaleString());
    }

    if (this.data.profilParameter === 'email') {
      return basicTest && /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,}$/.test(this.currValue as string);
    }

    if (!this.isString()) {
      return basicTest && this.currValue as number >= 1;
    }

    return basicTest;
  }

  isString(): boolean {
    return typeof this.data.value === 'string';
  }

  private formatDate(dateString: string): string {
    const parts = dateString.split(',')[0].split('.');

    if (parts[0].length === 1) {
      parts[0] = '0' + parts[0];
    }

    return `${parts[2]}-${parts[1]}-${parts[0]}`;
  }

  getLabel(): string {
    switch (this.data.profilParameter) {
      case 'firstName': return 'Imię';
      case 'lastName': return 'Nazwisko';
      case 'email': return 'Email';
      case 'sex': return 'Płeć';
      case 'birthDate': return 'Data urodzenia';
      case 'phoneNumber': return 'Numer telefonu';
      case 'height': return 'Wzrost';
      default: return '';
    }
  }

  getValue(): string | number | Date {
    if (this.data.profilParameter === 'birthDate') {
      return this.formatDate(this.currValue.toLocaleString());
    }

    return this.currValue;
  }
}
