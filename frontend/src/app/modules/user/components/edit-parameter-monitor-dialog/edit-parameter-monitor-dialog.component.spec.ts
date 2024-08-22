import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditParameterMonitorDialogComponent } from './edit-parameter-monitor-dialog.component';

describe('EditParameterMonitorDialogComponent', () => {
  let component: EditParameterMonitorDialogComponent;
  let fixture: ComponentFixture<EditParameterMonitorDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditParameterMonitorDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditParameterMonitorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
