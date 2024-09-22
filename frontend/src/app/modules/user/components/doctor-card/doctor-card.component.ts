import { Component, input, model, OnInit, output, signal } from '@angular/core';
import { DoctorResponse } from '../../../../core/models/doctor-response.model';

@Component({
  selector: 'app-doctor-card',
  templateUrl: './doctor-card.component.html',
  styleUrl: './doctor-card.component.scss'
})
export class DoctorCardComponent implements OnInit {
  doctor = model.required<DoctorResponse>();

  isInvitationSended = signal<boolean>(false);

  invitationEmit = output<DoctorResponse>();

  invitationDeleteEmit = output<DoctorResponse>();

  ngOnInit(): void {
    this.doctor.set({...this.doctor()});
  }

  getSpecializationsString(): string {
    return this.doctor().specializations.map(spec => spec.name).join(', ');
  }

  onInvite(): void {
    this.invitationEmit.emit(this.doctor()!);
    this.isInvitationSended.set(true);
  }

  onInviteDelete(): void {
    this.invitationDeleteEmit.emit(this.doctor()!);
    this.isInvitationSended.set(false);
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }
}
