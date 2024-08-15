import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CabinetCalendarComponent } from './cabinet-calendar.component';

describe('CabinetCalendarComponent', () => {
  let component: CabinetCalendarComponent;
  let fixture: ComponentFixture<CabinetCalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CabinetCalendarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CabinetCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
