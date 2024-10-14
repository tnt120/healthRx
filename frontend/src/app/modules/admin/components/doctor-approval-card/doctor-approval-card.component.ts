import { Component, input, OnInit, output, signal } from '@angular/core';
import { DoctorResponse } from '../../../../core/models/doctor-response.model';
import { ImageType } from '../../../../core/enums/image-type.enum';
@Component({
  selector: 'app-doctor-approval-card',
  templateUrl: './doctor-approval-card.component.html',
  styleUrl: './doctor-approval-card.component.scss'
})
export class DoctorApprovalCardComponent implements OnInit {
  doctor = input.required<DoctorResponse>();

  emitApprove = output<DoctorResponse>();

  emiReject = output<DoctorResponse>();

  photosPreview = signal<{ [key in keyof typeof ImageType]: string | null }>({
    PROFILE: null,
    FRONT_PWZ_PHOTO: null,
    BACK_PWZ_PHOTO: null,
  });

  ngOnInit(): void {
    this.photosPreview.set({
      PROFILE: `data:image/jpeg;base64 ,${this.doctor().pictureUrl}`,
      FRONT_PWZ_PHOTO: `data:image/jpeg;base64 ,${this.doctor().frontPwz}`,
      BACK_PWZ_PHOTO: `data:image/jpeg;base64 ,${this.doctor().backPwz}`,
    })
  }

  getSpecializationsString(): string {
    return this.doctor().specializations.map(spec => spec.name).join(', ');
  }

  approveDoctor(): void {
    this.emitApprove.emit(this.doctor());
  }

  rejectDoctor(): void {
    this.emiReject.emit(this.doctor());
  }

  isOverflow(e: HTMLElement): boolean {
    return e.scrollWidth <= e.clientWidth;
  }
}
