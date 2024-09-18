import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParametersManageDialogComponent } from './parameters-manage-dialog.component';

describe('ParametersManageDialogComponent', () => {
  let component: ParametersManageDialogComponent;
  let fixture: ComponentFixture<ParametersManageDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParametersManageDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParametersManageDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
