import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrugStatisticsComponent } from './drug-statistics.component';

describe('DrugStatisticsComponent', () => {
  let component: DrugStatisticsComponent;
  let fixture: ComponentFixture<DrugStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DrugStatisticsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DrugStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
