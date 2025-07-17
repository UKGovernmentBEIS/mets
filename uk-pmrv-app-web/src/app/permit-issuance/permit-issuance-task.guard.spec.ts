import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { mockPermitApplyPayload } from '@permit-application/testing/mock-permit-apply-action';
import { mockState } from '@permit-application/testing/mock-state';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { RequestActionsService, RequestItemsService, TasksService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitIssuanceTaskGuard } from './permit-issuance-task.guard';
import { PermitIssuanceStore } from './store/permit-issuance.store';

describe('PermitIssuanceTaskGuard', () => {
  let guard: PermitIssuanceTaskGuard;
  let store: PermitIssuanceStore;
  let authStore: AuthStore;
  let incorporateHeaderStore: IncorporateHeaderStore;

  const tasksService = mockClass(TasksService);
  const requestItemsService = mockClass(RequestItemsService);
  const requestActionsService = mockClass(RequestActionsService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: RequestItemsService, useValue: requestItemsService },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'OPERATOR' });
    guard = TestBed.inject(PermitIssuanceTaskGuard);
    store = TestBed.inject(PermitIssuanceStore);
    incorporateHeaderStore = TestBed.inject(IncorporateHeaderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit prepare payloads', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: { permit: {} } as any,
          type: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
        },
        requestInfo: { competentAuthority: 'ENGLAND' },
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: '1' }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: mockPermitApplyPayload,
          type: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
        },
        userAssignCapable: true,
        requestInfo: {
          competentAuthority: 'ENGLAND',
          paymentCompleted: false,
          type: 'PERMIT_ISSUANCE',
        },
      }),
    );
    const resetStoreSpy = jest.spyOn(store, 'reset');
    const incorporateHeaderStoreSpy = jest.spyOn(incorporateHeaderStore, 'reset');

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: mockState.requestTaskId }))),
    ).resolves.toBeTruthy();
    expect(resetStoreSpy).toHaveBeenCalledTimes(1);
    expect(incorporateHeaderStoreSpy).toHaveBeenCalledTimes(1);
    expect(store.getState().requestTaskId).toEqual(mockState.requestTaskId);
    expect(store.getState()).toEqual({
      ...mockState,
      assignable: undefined,
      requestId: undefined,
      requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_SUBMIT',
      accountId: undefined,
      permitType: undefined,
      reviewSectionsCompleted: {},
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_SAVE_APPLICATION'],
      isRequestTask: true,
      features: {},
    });
  });
});
