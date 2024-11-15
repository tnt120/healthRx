import { TestBed } from '@angular/core/testing';

import { StatisticsServiceService } from './statistics-service.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('StatisticsServiceService', () => {
  let service: StatisticsServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(StatisticsServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
