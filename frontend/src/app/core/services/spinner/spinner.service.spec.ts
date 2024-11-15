import { TestBed } from '@angular/core/testing';

import { SpinnerService } from './spinner.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';

describe('SpinnerService', () => {
  let service: SpinnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideExperimentalZonelessChangeDetection()]
    });
    service = TestBed.inject(SpinnerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
