import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CabinetDashboardComponent } from './cabinet-dashboard.component';

describe('CabinetDashboardComponent', () => {
  let component: CabinetDashboardComponent;
  let fixture: ComponentFixture<CabinetDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CabinetDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CabinetDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
