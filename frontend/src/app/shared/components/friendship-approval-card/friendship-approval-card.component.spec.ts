import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendshipApprovalCardComponent } from './friendship-approval-card.component';

describe('FriendshipApprovalCardComponent', () => {
  let component: FriendshipApprovalCardComponent;
  let fixture: ComponentFixture<FriendshipApprovalCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FriendshipApprovalCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendshipApprovalCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
