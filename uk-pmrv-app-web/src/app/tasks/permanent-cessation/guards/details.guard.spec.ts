import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { permanentCessationDetailsSummaryGuard } from './details.guard';

describe('detailsSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => permanentCessationDetailsSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
