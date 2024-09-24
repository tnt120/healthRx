import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendshipPermissionUpdateDialogComponent } from './friendship-permission-update-dialog.component';

describe('FriendshipPermissionUpdateDialogComponent', () => {
  let component: FriendshipPermissionUpdateDialogComponent;
  let fixture: ComponentFixture<FriendshipPermissionUpdateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FriendshipPermissionUpdateDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendshipPermissionUpdateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
