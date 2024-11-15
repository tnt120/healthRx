import { TestBed } from '@angular/core/testing';

import { FormErrorsService } from './form-errors.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';

describe('FormErrorsService', () => {
  let service: FormErrorsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideExperimentalZonelessChangeDetection()]
    });
    service = TestBed.inject(FormErrorsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
