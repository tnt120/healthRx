import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { doctorFactoryGuard } from './doctor-factory.guard';

describe('doctorFactoryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
      TestBed.runInInjectionContext(() => doctorFactoryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
