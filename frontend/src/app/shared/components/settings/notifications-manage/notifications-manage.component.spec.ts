import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificationsManageComponent } from './notifications-manage.component';

describe('NotificationsManageComponent', () => {
  let component: NotificationsManageComponent;
  let fixture: ComponentFixture<NotificationsManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NotificationsManageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotificationsManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
