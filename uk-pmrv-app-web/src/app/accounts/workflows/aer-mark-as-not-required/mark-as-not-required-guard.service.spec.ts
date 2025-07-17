import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { MarkAsNotRequiredGuard } from './mark-as-not-required-guard.service';

describe('MarkAsNotRequiredGuard', () => {
  let guard: MarkAsNotRequiredGuard;
  let mockRequestsService: RequestsService;
  let mockActivatedRoute: Partial<ActivatedRoute>;

  beforeEach(() => {
    mockRequestsService = mockClass(RequestsService);
    mockActivatedRoute = {
      paramMap: of(convertToParamMap({ 'request-id': 'abc123' })),
    };

    TestBed.configureTestingModule({
      providers: [
        MarkAsNotRequiredGuard,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: RequestsService, useValue: mockRequestsService },
      ],
    });

    guard = TestBed.inject(MarkAsNotRequiredGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should call hasAccessMarkAsNotRequired with the correct accountId', () => {
    const mockRouteSnapshot = {
      paramMap: convertToParamMap({ accountId: '123' }),
    } as ActivatedRouteSnapshot;

    mockRequestsService.hasAccessMarkAsNotRequired.mockReturnValueOnce(of(true));

    guard.canActivate(mockRouteSnapshot).subscribe();

    expect(mockRequestsService.hasAccessMarkAsNotRequired).toHaveBeenCalledTimes(1);
  });

  it('should return true when hasAccessMarkAsNotRequired returns true', (done) => {
    const mockRouteSnapshot = {
      paramMap: convertToParamMap({ accountId: '123' }),
    } as ActivatedRouteSnapshot;

    mockRequestsService.hasAccessMarkAsNotRequired.mockReturnValueOnce(of(true));

    guard.canActivate(mockRouteSnapshot).subscribe((canActivate) => {
      expect(canActivate).toBeTruthy();
      done();
    });
  });

  it('should return false when hasAccessMarkAsNotRequired returns false', (done) => {
    const mockRouteSnapshot = {
      paramMap: convertToParamMap({ accountId: '123' }),
    } as ActivatedRouteSnapshot;

    mockRequestsService.hasAccessMarkAsNotRequired.mockReturnValueOnce(of(false));

    guard.canActivate(mockRouteSnapshot).subscribe((canActivate) => {
      expect(canActivate).toBeFalsy();
      done();
    });
  });
});
