import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, of } from 'rxjs';

import { RequestActionDTO, RequestActionsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { RdeActionGuard } from './rde-action.guard';
import { initialState } from './store/rde.state';
import { RdeStore } from './store/rde.store';

describe('RdeActionGuard', () => {
  let guard: RdeActionGuard;
  let store: RdeStore;

  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(RdeActionGuard);
    store = TestBed.inject(RdeStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow Rde', async () => {
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
