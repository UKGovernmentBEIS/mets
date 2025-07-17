import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { returnToOperatorGuard } from './return-to-operator.guard';

describe('returnToOperatorGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => returnToOperatorGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
