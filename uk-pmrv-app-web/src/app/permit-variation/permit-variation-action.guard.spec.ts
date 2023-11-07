import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { RequestActionDTO, RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitVariationActionGuard } from './permit-variation-action.guard';
import { PermitVariationStore } from './store/permit-variation.store';

describe('PermitVariationActionGuard', () => {
  let guard: PermitVariationActionGuard;
  let store: PermitVariationStore;
  let authStore: AuthStore;
  let incorporateHeaderStore: IncorporateHeaderStore;

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'OPERATOR' });
    guard = TestBed.inject(PermitVariationActionGuard);
    store = TestBed.inject(PermitVariationStore);
    incorporateHeaderStore = TestBed.inject(IncorporateHeaderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit variation action', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(of({ type: 'PERM' } as any));

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({ type: 'PERMIT_VARIATION_APPLICATION_SUBMITTED' } as RequestActionDTO),
    );
    const resetStoreSpy = jest.spyOn(store, 'reset');
    const incorporateHeaderStoreSpy = jest.spyOn(incorporateHeaderStore, 'reset');

    await firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 123 })));

    expect(resetStoreSpy).toHaveBeenCalledTimes(1);
    expect(incorporateHeaderStoreSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().requestActionType).toEqual('PERMIT_VARIATION_APPLICATION_SUBMITTED');
    expect(store.getState().isRequestTask).toBeFalsy();
    expect(store.getState().assignable).toBeFalsy();
    expect(store.getState().isEditable).toBeFalsy();
    expect(store.getState().actionId).toEqual(123);
  });
});
