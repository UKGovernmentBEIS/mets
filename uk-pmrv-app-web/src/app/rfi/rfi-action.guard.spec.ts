import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { RequestActionDTO, RequestActionsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { RfiActionGuard } from './rfi-action.guard';
import { initialState } from './store/rfi.state';
import { RfiStore } from './store/rfi.store';

describe('RfiActionGuard', () => {
  let guard: RfiActionGuard;
  let store: RfiStore;

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(RfiActionGuard);
    store = TestBed.inject(RfiStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(of({ id: 1, payload: {} } as RequestActionDTO));

    await expect(
      firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 1 }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({ id: 1, requestType: 'PERMIT_ISSUANCE', competentAuthority: 'ENGLAND', payload: {} } as RequestActionDTO),
    );
    const resetSpy = jest.spyOn(store, 'reset');

    await firstValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: 1 })));

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual({
      ...initialState,
      actionId: 1,
      requestType: 'PERMIT_ISSUANCE',
      competentAuthority: 'ENGLAND',
      isEditable: false,
      requestActionCreationDate: undefined,
    });
  });
});
