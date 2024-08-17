import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HelloItemComponent } from './hello-item.component';

describe('HelloItemComponent', () => {
  let component: HelloItemComponent;
  let fixture: ComponentFixture<HelloItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HelloItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HelloItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
