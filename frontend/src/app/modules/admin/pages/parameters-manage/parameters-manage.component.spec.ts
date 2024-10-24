import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParametersManageComponent } from './parameters-manage.component';

describe('ParametersManageComponent', () => {
  let component: ParametersManageComponent;
  let fixture: ComponentFixture<ParametersManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParametersManageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParametersManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
