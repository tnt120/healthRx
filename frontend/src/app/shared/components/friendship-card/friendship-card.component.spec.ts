import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendshipCardComponent } from './friendship-card.component';

describe('FriendshipCardComponent', () => {
  let component: FriendshipCardComponent;
  let fixture: ComponentFixture<FriendshipCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FriendshipCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendshipCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
