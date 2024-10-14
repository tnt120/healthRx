import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DoctorApprovalsComponent } from './doctor-approvals.component';

describe('DoctorApprovalsComponent', () => {
  let component: DoctorApprovalsComponent;
  let fixture: ComponentFixture<DoctorApprovalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DoctorApprovalsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DoctorApprovalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
