import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctorApprovalCardComponent } from './doctor-approval-card.component';

describe('DoctorApprovalCardComponent', () => {
  let component: DoctorApprovalCardComponent;
  let fixture: ComponentFixture<DoctorApprovalCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DoctorApprovalCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DoctorApprovalCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
