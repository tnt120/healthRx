import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoleChangeDialogComponent } from './role-change-dialog.component';

describe('RoleChangeDialogComponent', () => {
  let component: RoleChangeDialogComponent;
  let fixture: ComponentFixture<RoleChangeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RoleChangeDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoleChangeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
