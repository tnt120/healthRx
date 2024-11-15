import { TestBed } from '@angular/core/testing';

import { ParametersService } from './parameters.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('ParametersService', () => {
  let service: ParametersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(ParametersService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
