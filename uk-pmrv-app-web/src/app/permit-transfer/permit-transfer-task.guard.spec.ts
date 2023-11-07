import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { mockPermitCompletePayload } from '@permit-application/testing/mock-permit-apply-action';
import { mockState } from '@permit-application/testing/mock-state';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitTransferTaskGuard } from './permit-transfer-task.guard';
import { PermitTransferStore } from './store/permit-transfer.store';
import { mockPermitTransferSubmitPayload } from './testing/mock';

describe('PermitTransferTaskGuard', () => {
  let guard: PermitTransferTaskGuard;
  let authStore: AuthStore;
  let store: PermitTransferStore;
  let incorporateHeaderStore: IncorporateHeaderStore;

  const tasksService = mockClass(TasksService);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'OPERATOR' });
    guard = TestBed.inject(PermitTransferTaskGuard);
    store = TestBed.inject(PermitTransferStore);
    incorporateHeaderStore = TestBed.inject(IncorporateHeaderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit transfer prepare payloads', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_TRANSFER_B_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: mockPermitTransferSubmitPayload,
          type: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
        },
        requestInfo: { competentAuthority: 'ENGLAND' },
      }),
    );

    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ taskId: mockState.requestTaskId }))),
    ).resolves.toBeTruthy();
  });

  it('should update the store upon task initialization', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_TRANSFER_B_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: {
            ...mockPermitTransferSubmitPayload,
            permitSectionsCompleted: {},
          },
          id: mockState.requestTaskId,
          type: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
        },
        requestInfo: {
          competentAuthority: 'ENGLAND',
          paymentCompleted: false,
        },
        userAssignCapable: true,
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
    expect(store.getState().permitSectionsCompleted).toEqual({
      ...mockPermitCompletePayload.permitSectionsCompleted,
      siteDiagrams: [false],
    });
  });

  it('should update the store upon task when sections already exist', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_TRANSFER_B_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: {
            ...mockPermitTransferSubmitPayload,
            permitSectionsCompleted: { siteDiagrams: [true] },
          },
          id: mockState.requestTaskId,
          type: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
        },
        requestInfo: {
          competentAuthority: 'ENGLAND',
          paymentCompleted: false,
        },
        userAssignCapable: true,
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
    expect(store.getState().permitSectionsCompleted).toEqual({ siteDiagrams: [true] });
  });
});
