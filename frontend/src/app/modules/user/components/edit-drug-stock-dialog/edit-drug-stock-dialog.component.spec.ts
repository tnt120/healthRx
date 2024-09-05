import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditDrugStockDialogComponent } from './edit-drug-stock-dialog.component';

describe('EditDrugStockDialogComponent', () => {
  let component: EditDrugStockDialogComponent;
  let fixture: ComponentFixture<EditDrugStockDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditDrugStockDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditDrugStockDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
