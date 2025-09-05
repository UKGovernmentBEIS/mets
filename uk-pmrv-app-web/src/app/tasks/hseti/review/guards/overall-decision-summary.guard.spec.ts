import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { HsetiOverallDecisionSummaryGuard } from './overall-decision-summary.guard';

describe('HsetiOverallDecisionSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => HsetiOverallDecisionSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
