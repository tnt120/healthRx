import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnverifiedDoctorComponent } from './unverified-doctor.component';

describe('UnverifiedDoctorComponent', () => {
  let component: UnverifiedDoctorComponent;
  let fixture: ComponentFixture<UnverifiedDoctorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UnverifiedDoctorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UnverifiedDoctorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
