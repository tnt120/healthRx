import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-reject-doctor-verification-dialog',
  templateUrl: './reject-doctor-verification-dialog.component.html',
  styleUrl: './reject-doctor-verification-dialog.component.scss'
})
export class RejectDoctorVerificationDialogComponent {
  message = signal<string>('');
}
