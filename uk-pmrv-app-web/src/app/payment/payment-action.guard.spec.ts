import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { RequestActionsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, MockType } from '../../testing';
import { PaymentActionGuard } from './payment-action.guard';
import { PaymentStore } from './store/payment.store';
import { mockPaymentActionState } from './testing/mock-state';

describe('PermitSurrenderActionGuard', () => {
  let guard: PaymentActionGuard;
  let store: PaymentStore;

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionById: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(PaymentActionGuard);
    store = TestBed.inject(PaymentStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should update store', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({
        id: mockPaymentActionState.requestActionId,
        requestId: mockPaymentActionState.requestId,
        requestType: mockPaymentActionState.requestType,
        competentAuthority: mockPaymentActionState.competentAuthority,
        payload: mockPaymentActionState.actionPayload,
        submitter: 'submitter',
        creationDate: mockPaymentActionState.requestActionCreationDate,
      }),
    );

    const resetSpy = jest.spyOn(store, 'reset');

    await expect(
      lastValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: `${mockPaymentActionState.requestActionId}` })),
      ),
    ).resolves.toBeTruthy();

    expect(requestActionsService.getRequestActionById).toHaveBeenCalledTimes(1);
    expect(requestActionsService.getRequestActionById).toHaveBeenLastCalledWith(mockPaymentActionState.requestActionId);

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual(mockPaymentActionState);
  });
});
