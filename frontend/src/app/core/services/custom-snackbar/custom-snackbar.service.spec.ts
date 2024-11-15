import { TestBed } from '@angular/core/testing';

import { CustomSnackbarService } from './custom-snackbar.service';
import { provideExperimentalZonelessChangeDetection } from '@angular/core';

describe('CustomSnackbarService', () => {
  let service: CustomSnackbarService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideExperimentalZonelessChangeDetection()]
    });
    service = TestBed.inject(CustomSnackbarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
