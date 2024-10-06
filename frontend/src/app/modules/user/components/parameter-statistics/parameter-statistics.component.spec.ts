import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParameterStatisticsComponent } from './parameter-statistics.component';

describe('ParameterStatisticsComponent', () => {
  let component: ParameterStatisticsComponent;
  let fixture: ComponentFixture<ParameterStatisticsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParameterStatisticsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParameterStatisticsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
