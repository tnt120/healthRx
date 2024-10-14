import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReVerifyComponent } from './re-verify.component';

describe('ReVerifyComponent', () => {
  let component: ReVerifyComponent;
  let fixture: ComponentFixture<ReVerifyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReVerifyComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReVerifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
