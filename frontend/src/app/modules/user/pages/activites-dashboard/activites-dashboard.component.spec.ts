import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitesDashboardComponent } from './activites-dashboard.component';

describe('ActivitesDashboardComponent', () => {
  let component: ActivitesDashboardComponent;
  let fixture: ComponentFixture<ActivitesDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivitesDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivitesDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
