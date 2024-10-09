import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityTileComponent } from './activity-tile.component';

describe('ActivityTileComponent', () => {
  let component: ActivityTileComponent;
  let fixture: ComponentFixture<ActivityTileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityTileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityTileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
