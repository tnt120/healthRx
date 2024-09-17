import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserProfileEditDialogComponent } from './user-profile-edit-dialog.component';

describe('UserProfileEditDialogComponent', () => {
  let component: UserProfileEditDialogComponent;
  let fixture: ComponentFixture<UserProfileEditDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserProfileEditDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserProfileEditDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
