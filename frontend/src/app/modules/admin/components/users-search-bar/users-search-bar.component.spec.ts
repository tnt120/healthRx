import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersSearchBarComponent } from './users-search-bar.component';

describe('UsersSearchBarComponent', () => {
  let component: UsersSearchBarComponent;
  let fixture: ComponentFixture<UsersSearchBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UsersSearchBarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UsersSearchBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
