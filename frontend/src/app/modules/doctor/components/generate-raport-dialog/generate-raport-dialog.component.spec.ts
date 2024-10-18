import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateRaportDialogComponent } from './generate-raport-dialog.component';

describe('GenerateRaportDialogComponent', () => {
  let component: GenerateRaportDialogComponent;
  let fixture: ComponentFixture<GenerateRaportDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateRaportDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerateRaportDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
