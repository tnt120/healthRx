import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageUserActivityDialogComponent } from './manage-user-activity-dialog.component';

describe('ManageUserActivityDialogComponent', () => {
  let component: ManageUserActivityDialogComponent;
  let fixture: ComponentFixture<ManageUserActivityDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ManageUserActivityDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManageUserActivityDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
