import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { overallDecisionSummaryGuard } from './overall-decision-summary.guard';

describe('overallDecisionSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => overallDecisionSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
