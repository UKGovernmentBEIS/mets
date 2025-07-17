import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { opinionStatementSummaryGuard } from './opinion-statement-summary.guard';

describe('opinionStatementSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => opinionStatementSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
