import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { OverallDecisionSummaryGuard } from './overall-decision-summary.guard';

describe('OverallDecisionSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => OverallDecisionSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
