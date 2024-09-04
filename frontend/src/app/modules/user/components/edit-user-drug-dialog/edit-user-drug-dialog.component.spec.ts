import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditUserDrugDialogComponent } from './edit-user-drug-dialog.component';

describe('EditUserDrugDialogComponent', () => {
  let component: EditUserDrugDialogComponent;
  let fixture: ComponentFixture<EditUserDrugDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditUserDrugDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditUserDrugDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
