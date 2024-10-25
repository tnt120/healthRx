import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component, inject, OnInit, signal } from '@angular/core';
import { UserResponse } from '../../../../core/models/user/user-response.model';
import { Roles } from '../../../../core/enums/roles.enum';

@Component({
  selector: 'app-role-change-dialog',
  templateUrl: './role-change-dialog.component.html',
  styleUrl: './role-change-dialog.component.scss'
})
export class RoleChangeDialogComponent implements OnInit {
  protected user: UserResponse = inject(MAT_DIALOG_DATA);

  newRole = signal<Roles | null>(null);

  roles = signal<{role: Roles, translation: string}[]>([
    {
      role: Roles.USER,
      translation: 'Użytkownik'
    },
    {
      role: Roles.ADMIN,
      translation: 'Administrator'
    },
  ]);

  ngOnInit(): void {
    this.newRole.set(this.user.role);
  }

  protected isValid(): boolean {
    return this.newRole() !== this.user.role;
  }
}
