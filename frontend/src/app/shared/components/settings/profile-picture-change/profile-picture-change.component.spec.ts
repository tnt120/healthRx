import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfilePictureChangeComponent } from './profile-picture-change.component';

describe('ProfilePictureChangeComponent', () => {
  let component: ProfilePictureChangeComponent;
  let fixture: ComponentFixture<ProfilePictureChangeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProfilePictureChangeComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProfilePictureChangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
