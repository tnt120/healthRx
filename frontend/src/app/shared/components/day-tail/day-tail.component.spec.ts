import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DayTailComponent } from './day-tail.component';

describe('DayTailComponent', () => {
  let component: DayTailComponent;
  let fixture: ComponentFixture<DayTailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DayTailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DayTailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
