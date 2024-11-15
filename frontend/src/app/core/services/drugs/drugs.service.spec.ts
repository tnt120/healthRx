import { TestBed } from '@angular/core/testing';

import { DrugsService } from './drugs.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

describe('DrugsService', () => {
  let service: DrugsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideExperimentalZonelessChangeDetection(),
        provideHttpClient(),
        provideHttpClientTesting(),
      ]
    });
    service = TestBed.inject(DrugsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
