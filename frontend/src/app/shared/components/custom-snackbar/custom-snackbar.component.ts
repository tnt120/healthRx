import { Component, inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-custom-snackbar',
  templateUrl: './custom-snackbar.component.html',
  styleUrl: './custom-snackbar.component.scss'
})
export class CustomSnackbarComponent {
  protected readonly data: { title: string, message: string, type: string, snackBar: MatSnackBar } = inject(MAT_SNACK_BAR_DATA);

  closeSnackbar() {
    this.data.snackBar.dismiss();
  }
}
