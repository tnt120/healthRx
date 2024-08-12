import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TextSummaryRowComponent } from './text-summary-row.component';

describe('TextSummaryRowComponent', () => {
  let component: TextSummaryRowComponent;
  let fixture: ComponentFixture<TextSummaryRowComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TextSummaryRowComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TextSummaryRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
