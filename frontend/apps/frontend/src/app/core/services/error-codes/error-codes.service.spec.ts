import { TestBed } from '@angular/core/testing';

import { ErrorCodesService } from './error-codes.service';

describe('ErrorCodesService', () => {
  let service: ErrorCodesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ErrorCodesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
