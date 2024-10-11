import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivityStatisticsComponent } from './activity-statistics.component';

describe('ActivityStatisticsComponent', () => {
  let component: ActivityStatisticsComponent;
  let fixture: ComponentFixture<ActivityStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivityStatisticsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivityStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
