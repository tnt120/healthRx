import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityManageDialogComponent } from './activity-manage-dialog.component';

describe('ActivityManageDialogComponent', () => {
  let component: ActivityManageDialogComponent;
  let fixture: ComponentFixture<ActivityManageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityManageDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityManageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
