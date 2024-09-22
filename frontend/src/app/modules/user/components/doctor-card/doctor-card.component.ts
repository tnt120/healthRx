import { Subscription } from 'rxjs';
import { Component, inject, input, model, OnDestroy, OnInit, output, signal } from '@angular/core';
import { DoctorResponse } from '../../../../core/models/doctor-response.model';
import { FriendshipService } from '../../../../core/services/friendship/friendship.service';

@Component({
  selector: 'app-doctor-card',
  templateUrl: './doctor-card.component.html',
  styleUrl: './doctor-card.component.scss'
})
export class DoctorCardComponent implements OnInit, OnDestroy {
  private readonly friendshipService = inject(FriendshipService);

  doctor = model.required<DoctorResponse>();

  isInvitationSended = signal<boolean>(false);

  friendshipId = signal<string>('');

  subscriptions: Subscription[] = [];

  ngOnInit(): void {
    this.doctor.set({...this.doctor()});
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  getSpecializationsString(): string {
    return this.doctor().specializations.map(spec => spec.name).join(', ');
  }

  onInvite(): void {
    this.friendshipService.sendInvitation({ targetDoctorId: this.doctor().id }).subscribe({
      next: res => {
        this.isInvitationSended.set(true);
        this.friendshipId.set(res.friendshipId);
      },
      error: err => {
        console.error(err);
      }
    })
  }

  onInviteDelete(): void {
    this.friendshipService.cancelInvitation(this.friendshipId()).subscribe({
      next: (res) => {
        this.isInvitationSended.set(false);
        this.friendshipId.set('');
      },
      error: (err) => {
        console.error(err);
      }
    })
    this.isInvitationSended.set(false);
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }
}
