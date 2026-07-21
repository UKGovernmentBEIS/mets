import { DOCUMENT } from '@angular/common';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, CanActivateFn, RouterStateSnapshot } from '@angular/router';

import { ActivatedRouteSnapshotStub } from '@testing';

import { externalRedirectGuard } from './external-redirect.guard';

describe('externalRedirectGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
    TestBed.runInInjectionContext(() => externalRedirectGuard(...guardParameters));

  const mockLocation = { replace: jest.fn() };
  const mockDocument = { location: mockLocation };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DOCUMENT, useValue: mockDocument }],
    });
    mockLocation.replace.mockClear();
  });

  it('should redirect to the configured external URL', () => {
    executeGuard(
      new ActivatedRouteSnapshotStub({}, {}, { externalUrl: '/contact-us' }) as ActivatedRouteSnapshot,
      {} as RouterStateSnapshot,
    );

    expect(mockLocation.replace).toHaveBeenCalledWith('/contact-us');
  });

  it('should return false to cancel Angular navigation', () => {
    const result = executeGuard(
      new ActivatedRouteSnapshotStub({}, {}, { externalUrl: '/contact-us' }) as ActivatedRouteSnapshot,
      {} as RouterStateSnapshot,
    );

    expect(result).toBe(false);
  });
});
