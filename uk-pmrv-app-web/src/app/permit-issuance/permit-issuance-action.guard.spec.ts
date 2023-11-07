import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { PermitIssuanceActionGuard } from './permit-issuance-action.guard';
import { PermitIssuanceStore } from './store/permit-issuance.store';

describe('PermitIssuanceActionGuard', () => {
  let guard: PermitIssuanceActionGuard;
  let store: PermitIssuanceStore;
  let authStore: AuthStore;

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'OPERATOR' });
    guard = TestBed.inject(PermitIssuanceActionGuard);
    store = TestBed.inject(PermitIssuanceStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({ type: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED' } as any),
    );

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({ type: 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED' } as any),
    );
    const resetSpy = jest.spyOn(store, 'reset');

    await firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 })));

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().requestActionType).toEqual('PERMIT_ISSUANCE_APPLICATION_SUBMITTED');
    expect(store.getState().isRequestTask).toBeFalsy();
    expect(store.getState().isEditable).toBeFalsy();
    expect(store.getState().assignable).toBeFalsy();
  });
});
