import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { RequestActionDTO, RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitTransferActionGuard } from './permit-transfer-action.guard';
import { PermitTransferStore } from './store/permit-transfer.store';

describe('PermitTransferActionGuardTsGuard', () => {
  let guard: PermitTransferActionGuard;
  let store: PermitTransferStore;
  let incorporateHeaderStore: IncorporateHeaderStore;
  let authStore: AuthStore;

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'OPERATOR' });
    guard = TestBed.inject(PermitTransferActionGuard);
    store = TestBed.inject(PermitTransferStore);
    incorporateHeaderStore = TestBed.inject(IncorporateHeaderStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit transfer action', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(of({ type: 'PERM' } as any));

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({ type: 'PERMIT_TRANSFER_B_APPLICATION_SUBMITTED' } as RequestActionDTO),
    );
    const resetStoreSpy = jest.spyOn(store, 'reset');
    const incorporateHeaderStoreSpy = jest.spyOn(incorporateHeaderStore, 'reset');

    await firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 })));

    expect(resetStoreSpy).toHaveBeenCalledTimes(1);
    expect(incorporateHeaderStoreSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().requestActionType).toEqual('PERMIT_TRANSFER_B_APPLICATION_SUBMITTED');
    expect(store.getState().isRequestTask).toBeFalsy();
    expect(store.getState().assignable).toBeFalsy();
    expect(store.getState().isEditable).toBeFalsy();
    expect(store.getState().actionId).toEqual(123);
  });
});
