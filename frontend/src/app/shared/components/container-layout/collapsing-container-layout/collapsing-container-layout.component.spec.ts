import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CollapsingContainerLayoutComponent } from './collapsing-container-layout.component';

describe('CollapsingContainerLayoutComponent', () => {
  let component: CollapsingContainerLayoutComponent;
  let fixture: ComponentFixture<CollapsingContainerLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CollapsingContainerLayoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CollapsingContainerLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
