import { UnverifiedDoctor } from './../../../../core/models/user/unverified-doctor-model';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { map, Observable, Subscription } from 'rxjs';
import { ImageService } from '../../../../core/services/image/image.service';
import { UserResponse } from '../../../../core/models/user/user-response.model';

@Component({
  selector: 'app-unverified-doctor',
  templateUrl: './unverified-doctor.component.html',
  styleUrl: './unverified-doctor.component.scss'
})
export class UnverifiedDoctorComponent {
  private readonly store = inject(Store);

  user$: Observable<UserResponse> = this.store.select('user');
}
