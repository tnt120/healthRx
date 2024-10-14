import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PreviewDoctorDataComponent } from './preview-doctor-data.component';

describe('PreviewDoctorDataComponent', () => {
  let component: PreviewDoctorDataComponent;
  let fixture: ComponentFixture<PreviewDoctorDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PreviewDoctorDataComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PreviewDoctorDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
