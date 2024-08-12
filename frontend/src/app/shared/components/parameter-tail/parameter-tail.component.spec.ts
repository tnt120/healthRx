import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParameterTailComponent } from './parameter-tail.component';

describe('ParameterTailComponent', () => {
  let component: ParameterTailComponent;
  let fixture: ComponentFixture<ParameterTailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParameterTailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParameterTailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
