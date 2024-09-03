import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserDrugsDetailsComponent } from './user-drugs-details.component';

describe('UserDrugsDetailsComponent', () => {
  let component: UserDrugsDetailsComponent;
  let fixture: ComponentFixture<UserDrugsDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserDrugsDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserDrugsDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
