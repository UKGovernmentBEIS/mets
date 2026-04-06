import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { nerSendReportGuard } from './send-report.guard';

describe('sendReportGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => nerSendReportGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
