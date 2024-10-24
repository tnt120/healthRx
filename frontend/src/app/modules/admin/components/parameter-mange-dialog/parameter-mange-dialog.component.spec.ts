import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParameterMangeDialogComponent } from './parameter-mange-dialog.component';

describe('ParameterMangeDialogComponent', () => {
  let component: ParameterMangeDialogComponent;
  let fixture: ComponentFixture<ParameterMangeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ParameterMangeDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParameterMangeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
