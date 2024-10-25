import { Component, inject, signal } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserResponse } from '../../../../core/models/user/user-response.model';

@Component({
  selector: 'app-delete-user-dialog',
  templateUrl: './delete-user-dialog.component.html',
  styleUrl: './delete-user-dialog.component.scss'
})
export class DeleteUserDialogComponent {
  protected data: UserResponse = inject(MAT_DIALOG_DATA);

  message = signal<string>('');
}
