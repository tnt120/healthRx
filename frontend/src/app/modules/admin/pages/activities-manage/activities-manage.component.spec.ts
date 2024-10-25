import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivitiesManageComponent } from './activities-manage.component';

describe('ActivitiesManageComponent', () => {
  let component: ActivitiesManageComponent;
  let fixture: ComponentFixture<ActivitiesManageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ActivitiesManageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivitiesManageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
