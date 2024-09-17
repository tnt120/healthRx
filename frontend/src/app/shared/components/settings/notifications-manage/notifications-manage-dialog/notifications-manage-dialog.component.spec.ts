import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationsManageDialogComponent } from './notifications-manage-dialog.component';

describe('NotificationsManageDialogComponent', () => {
  let component: NotificationsManageDialogComponent;
  let fixture: ComponentFixture<NotificationsManageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationsManageDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationsManageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
