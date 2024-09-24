import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendshipFilterPanelComponent } from './friendship-filter-panel.component';

describe('FriendshipFilterPanelComponent', () => {
  let component: FriendshipFilterPanelComponent;
  let fixture: ComponentFixture<FriendshipFilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FriendshipFilterPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendshipFilterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
