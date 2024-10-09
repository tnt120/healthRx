import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityEditTileComponent } from './activity-edit-tile.component';

describe('ActivityEditTileComponent', () => {
  let component: ActivityEditTileComponent;
  let fixture: ComponentFixture<ActivityEditTileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityEditTileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityEditTileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
