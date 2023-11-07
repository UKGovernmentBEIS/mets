import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { RequestActionPayload, RequestActionsService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, MockType } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { PermitSurrenderActionGuard } from './permit-surrender-action.guard';
import { PermitSurrenderStore } from './store/permit-surrender.store';
import { mockActionState, mockActionSubmittedPayload } from './testing/mock-state';

describe('PermitSurrenderActionGuard', () => {
  let guard: PermitSurrenderActionGuard;
  let store: PermitSurrenderStore;

  const requestActionsService: MockType<RequestActionsService> = {
    getRequestActionById: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: RequestActionsService, useValue: requestActionsService }],
    });
    guard = TestBed.inject(PermitSurrenderActionGuard);
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit surrender payloads', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({
        id: mockActionState.requestActionId,
        type: mockActionState.requestActionType,
        payload: {
          payloadType: mockActionSubmittedPayload.payloadType,
          permitSurrender: mockActionSubmittedPayload.permitSurrender,
          permitSurrenderAttachments: mockActionSubmittedPayload.permitSurrenderAttachments,
        } as RequestActionPayload,
        submitter: 'submitter',
        creationDate: mockActionState.requestActionCreationDate,
        requestId: mockActionState.requestId,
        requestType: mockActionState.requestType,
        competentAuthority: mockActionState.competentAuthority,
      }),
    );

    await expect(
      lastValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: `${mockActionState.requestActionId}` })),
      ),
    ).resolves.toBeTruthy();

    expect(requestActionsService.getRequestActionById).toHaveBeenCalledTimes(1);
    expect(requestActionsService.getRequestActionById).toHaveBeenLastCalledWith(mockActionState.requestActionId);
  });

  it('should update store', async () => {
    requestActionsService.getRequestActionById.mockReturnValueOnce(
      of({
        id: mockActionState.requestActionId,
        type: mockActionState.requestActionType,
        payload: {
          payloadType: mockActionSubmittedPayload.payloadType,
          permitSurrender: mockActionSubmittedPayload.permitSurrender,
          permitSurrenderAttachments: mockActionSubmittedPayload.permitSurrenderAttachments,
        } as RequestActionPayload,
        submitter: mockActionState.requestActionSubmitter,
        creationDate: mockActionState.requestActionCreationDate,
        requestId: mockActionState.requestId,
        requestType: mockActionState.requestType,
        competentAuthority: mockActionState.competentAuthority,
      }),
    );

    const resetSpy = jest.spyOn(store, 'reset');

    await expect(
      lastValueFrom(
        guard.canActivate(new ActivatedRouteSnapshotStub({ actionId: `${mockActionState.requestActionId}` })),
      ),
    ).resolves.toBeTruthy();

    expect(requestActionsService.getRequestActionById).toHaveBeenCalledTimes(1);
    expect(requestActionsService.getRequestActionById).toHaveBeenLastCalledWith(mockActionState.requestActionId);

    expect(resetSpy).toHaveBeenCalledTimes(1);
    expect(store.getState()).toEqual(mockActionState);
  });
});
