import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SnackBarData } from '../../models/snackbar-data.model';
import { CustomSnackbarComponent } from '../../../shared/components/custom-snackbar/custom-snackbar.component';

@Injectable({
  providedIn: 'root'
})
export class CustomSnackbarService {
  private readonly _snackbar = inject(MatSnackBar);

  openCustomSnackbar(data: SnackBarData) {
    this._snackbar.openFromComponent(CustomSnackbarComponent, {
      duration: data.duration,
      horizontalPosition: 'end',
      verticalPosition: 'bottom',
      data: {
        title: data.title,
        message: data.message,
        type: data.type,
        snackBar: this._snackbar
      },
      panelClass: this.getPanelClass(data.type)
    })
  }

  private getPanelClass(type: string): string {
    switch (type) {
      case 'success':
        return 'success-snackbar';
      case 'error':
        return 'error-snackbar';
      case 'warning':
        return 'warning-snackbar';
      default:
        return 'default-snackbar';
    }
  }
}
