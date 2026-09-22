import { TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { mockClass } from '@testing';

import { RequestsService } from 'pmrv-api';

import { PermitVariationStore } from '../../store/permit-variation.store';
import { RequestPaymentGuard } from './request-payment.guard';

describe('RequestPaymentGuard', () => {
  let guard: RequestPaymentGuard;
  let mockRequestsService: RequestsService;
  let store: PermitVariationStore;

  beforeEach(() => {
    mockRequestsService = mockClass(RequestsService);

    TestBed.configureTestingModule({
      providers: [RequestPaymentGuard, { provide: RequestsService, useValue: mockRequestsService }],
    });

    guard = TestBed.inject(RequestPaymentGuard);
    store = TestBed.inject(PermitVariationStore);
    store.setState({ ...store.getState(), requestId: 'REQ-1' });
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should call hasAccessRequestPayment with the request id from the store', () => {
    mockRequestsService.hasAccessRequestPayment.mockReturnValueOnce(of(true));

    guard.canActivate().subscribe();

    expect(mockRequestsService.hasAccessRequestPayment).toHaveBeenCalledWith('REQ-1');
  });

  it('should return true when hasAccessRequestPayment returns true', (done) => {
    mockRequestsService.hasAccessRequestPayment.mockReturnValueOnce(of(true));

    guard.canActivate().subscribe((canActivate) => {
      expect(canActivate).toBeTruthy();
      done();
    });
  });

  it('should return false when hasAccessRequestPayment returns false', (done) => {
    mockRequestsService.hasAccessRequestPayment.mockReturnValueOnce(of(false));

    guard.canActivate().subscribe((canActivate) => {
      expect(canActivate).toBeFalsy();
      done();
    });
  });
});
