import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { alcInformationSummaryGuard } from './alc-information-summary.guard';

describe('alcInformationSummaryGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => alcInformationSummaryGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
