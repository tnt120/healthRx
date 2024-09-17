import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsUserProfileComponent } from './settings-user-profile.component';

describe('SettingsUserProfileComponent', () => {
  let component: SettingsUserProfileComponent;
  let fixture: ComponentFixture<SettingsUserProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SettingsUserProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SettingsUserProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
