import { Component, inject, OnInit, signal } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FriendshipPermissions } from '../../../core/models/friendship-response.model';

@Component({
  selector: 'app-friendship-permission-update-dialog',
  templateUrl: './friendship-permission-update-dialog.component.html',
  styleUrl: './friendship-permission-update-dialog.component.scss'
})
export class FriendshipPermissionUpdateDialogComponent implements OnInit {
  data: FriendshipPermissions = inject(MAT_DIALOG_DATA);

  currPermissions = signal<FriendshipPermissions>({
    userMedicineAccess: false,
    activitiesAccess: false,
    parametersAccess: false
  });

  ngOnInit(): void {
    this.currPermissions.set({ ...this.data });
  }

  isValid(): boolean {
    return JSON.stringify(this.data) !== JSON.stringify(this.currPermissions());
  }
}
