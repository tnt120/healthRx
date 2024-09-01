import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TakeDrugMonitorDialogComponent } from './take-drug-monitor-dialog.component';

describe('TakeDrugMonitorDialogComponent', () => {
  let component: TakeDrugMonitorDialogComponent;
  let fixture: ComponentFixture<TakeDrugMonitorDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TakeDrugMonitorDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TakeDrugMonitorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
