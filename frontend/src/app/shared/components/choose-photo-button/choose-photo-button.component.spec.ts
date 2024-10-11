import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChoosePhotoButtonComponent } from './choose-photo-button.component';

describe('ChoosePhotoButtonComponent', () => {
  let component: ChoosePhotoButtonComponent;
  let fixture: ComponentFixture<ChoosePhotoButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChoosePhotoButtonComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChoosePhotoButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
