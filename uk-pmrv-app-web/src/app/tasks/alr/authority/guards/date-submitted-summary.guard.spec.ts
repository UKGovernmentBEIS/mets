import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { dateSubmittedSummaryGuard } from './date-submitted-summary.guard';

describe('dateSubmittedSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => dateSubmittedSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
