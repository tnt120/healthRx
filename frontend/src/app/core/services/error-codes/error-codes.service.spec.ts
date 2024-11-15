import { TestBed } from '@angular/core/testing';

import { ErrorCodesService } from './error-codes.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';

describe('ErrorCodesService', () => {
  let service: ErrorCodesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideExperimentalZonelessChangeDetection()]
    });
    service = TestBed.inject(ErrorCodesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
