import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { outcomeSummaryGuard } from './outcome-summary.guard';

describe('outcomeSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => outcomeSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
