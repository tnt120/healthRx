import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchDoctorsFilterPanelComponent } from './search-doctors-filter-panel.component';

describe('SearchDoctorsFilterPanelComponent', () => {
  let component: SearchDoctorsFilterPanelComponent;
  let fixture: ComponentFixture<SearchDoctorsFilterPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SearchDoctorsFilterPanelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchDoctorsFilterPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
