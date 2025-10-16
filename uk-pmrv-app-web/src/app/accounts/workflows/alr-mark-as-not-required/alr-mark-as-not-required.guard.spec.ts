import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, ActivatedRouteSnapshot, convertToParamMap } from '@angular/router';

import { of } from 'rxjs';

import { mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { AlrMarkAsNotRequiredGuard } from './alr-mark-as-not-required.guard';

describe('AlrMarkAsNotRequiredGuard', () => {
  let guard: AlrMarkAsNotRequiredGuard;
  let mockRequestsService: RequestsService;
  let mockActivatedRoute: Partial<ActivatedRoute>;

  beforeEach(() => {
    mockRequestsService = mockClass(RequestsService);
    mockActivatedRoute = {
      paramMap: of(convertToParamMap({ 'request-id': 'abc123' })),
    };

    TestBed.configureTestingModule({
      providers: [
        AlrMarkAsNotRequiredGuard,
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: RequestsService, useValue: mockRequestsService },
      ],
    });

    guard = TestBed.inject(AlrMarkAsNotRequiredGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should call hasAccessMarkAsNotRequiredAlr with the correct accountId', () => {
    const mockRouteSnapshot = {
      paramMap: convertToParamMap({ accountId: '123' }),
    } as ActivatedRouteSnapshot;

    mockRequestsService.hasAccessMarkAsNotRequiredAlr.mockReturnValueOnce(of(true));

    guard.canActivate(mockRouteSnapshot).subscribe();

    expect(mockRequestsService.hasAccessMarkAsNotRequiredAlr).toHaveBeenCalledTimes(1);
  });

  it('should return true when hasAccessMarkAsNotRequiredAlr returns true', (done) => {
    const mockRouteSnapshot = {
      paramMap: convertToParamMap({ accountId: '123' }),
    } as ActivatedRouteSnapshot;

    mockRequestsService.hasAccessMarkAsNotRequiredAlr.mockReturnValueOnce(of(true));

    guard.canActivate(mockRouteSnapshot).subscribe((canActivate) => {
      expect(canActivate).toBeTruthy();
      done();
    });
  });

  it('should return false when hasAccessMarkAsNotRequiredAlr returns false', (done) => {
    const mockRouteSnapshot = {
      paramMap: convertToParamMap({ accountId: '123' }),
    } as ActivatedRouteSnapshot;

    mockRequestsService.hasAccessMarkAsNotRequiredAlr.mockReturnValueOnce(of(false));

    guard.canActivate(mockRouteSnapshot).subscribe((canActivate) => {
      expect(canActivate).toBeFalsy();
      done();
    });
  });
});
