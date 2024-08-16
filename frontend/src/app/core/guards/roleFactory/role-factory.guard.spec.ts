import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { roleFactoryGuard } from './role-factory.guard';

describe('roleFactoryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => roleFactoryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
