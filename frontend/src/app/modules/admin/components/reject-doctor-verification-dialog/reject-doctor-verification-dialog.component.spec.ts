import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RejectDoctorVerificationDialogComponent } from './reject-doctor-verification-dialog.component';

describe('RejectDoctorVerificationDialogComponent', () => {
  let component: RejectDoctorVerificationDialogComponent;
  let fixture: ComponentFixture<RejectDoctorVerificationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RejectDoctorVerificationDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RejectDoctorVerificationDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
