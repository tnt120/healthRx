import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DrugItemComponent } from './drug-item.component';

describe('DrugItemComponent', () => {
  let component: DrugItemComponent;
  let fixture: ComponentFixture<DrugItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DrugItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DrugItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
