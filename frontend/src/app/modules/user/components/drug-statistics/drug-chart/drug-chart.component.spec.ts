import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrugChartComponent } from './drug-chart.component';

describe('DrugChartComponent', () => {
  let component: DrugChartComponent;
  let fixture: ComponentFixture<DrugChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DrugChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DrugChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
