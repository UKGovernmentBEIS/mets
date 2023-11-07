import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { AuthStore } from '@core/store/auth';
import { mockPermitCompletePayload } from '@permit-application/testing/mock-permit-apply-action';
import { mockState } from '@permit-application/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteSnapshotStub, mockClass } from '@testing';

import { RequestActionsService, RequestItemsService, TasksService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitVariationTaskGuard } from './permit-variation-task.guard';
import { PermitVariationStore } from './store/permit-variation.store';
import { mockPermitVariationSubmitOperatorLedPayload } from './testing/mock';

describe('PermitVariationTaskGuard', () => {
  let guard: PermitVariationTaskGuard;
  let store: PermitVariationStore;
  let authStore: AuthStore;
  let commonStore: CommonTasksStore;
  const requestItemsService = mockClass(RequestItemsService);
  const requestActionsService = mockClass(RequestActionsService);
  let incorporateHeaderStore: IncorporateHeaderStore;

  const tasksService = mockClass(TasksService);

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
    commonStore = TestBed.inject(CommonTasksStore);
    commonStore.setState({
      ...commonStore.getState(),
      requestTaskItem: {
        ...commonStore.getState().requestTaskItem,
        requestTask: { type: 'PERMIT_VARIATION_APPLICATION_SUBMIT' },
      },
    });
    guard = TestBed.inject(PermitVariationTaskGuard);
    store = TestBed.inject(PermitVariationStore);
    incorporateHeaderStore = TestBed.inject(IncorporateHeaderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow permit variation prepare payloads', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: mockPermitVariationSubmitOperatorLedPayload,
          type: 'PERMIT_VARIATION_APPLICATION_SUBMIT',
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
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: {
            ...mockPermitVariationSubmitOperatorLedPayload,
            permitSectionsCompleted: {},
            reviewSectionsCompleted: {},
          },
          id: mockState.requestTaskId,
          type: 'PERMIT_VARIATION_APPLICATION_SUBMIT',
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
    expect(store.getState().permitSectionsCompleted).toEqual(mockPermitCompletePayload.permitSectionsCompleted);
    expect(store.getState().reviewSectionsCompleted).toEqual({
      ADDITIONAL_INFORMATION: true,
      CONFIDENTIALITY_STATEMENT: true,
      DEFINE_MONITORING_APPROACHES: true,
      FALLBACK: true,
      FUELS_AND_EQUIPMENT: true,
      INHERENT_CO2: true,
      INSTALLATION_DETAILS: true,
      MANAGEMENT_PROCEDURES: true,
      MEASUREMENT_CO2: true,
      MONITORING_METHODOLOGY_PLAN: true,
      MEASUREMENT_N2O: true,
      PERMIT_TYPE: true,
      CALCULATION_PFC: true,
      TRANSFERRED_CO2_N2O: true,
      UNCERTAINTY_ANALYSIS: true,
      determination: true,
    });
  });

  it('should update the store upon task when sections already exist', async () => {
    tasksService.getTaskItemInfoById.mockReturnValueOnce(
      of({
        allowedRequestTaskActions: ['PERMIT_VARIATION_SAVE_APPLICATION'],
        requestTask: {
          assigneeFullName: 'John Doe',
          assigneeUserId: '100',
          payload: {
            ...mockPermitVariationSubmitOperatorLedPayload,
            permitSectionsCompleted: { sourceStreams: [true] },
            reviewSectionsCompleted: { FUELS_AND_EQUIPMENT: false },
          },
          id: mockState.requestTaskId,
          type: 'PERMIT_VARIATION_APPLICATION_SUBMIT',
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
    expect(store.getState().permitSectionsCompleted).toEqual({ sourceStreams: [true] });
    expect(store.getState().reviewSectionsCompleted).toEqual({ FUELS_AND_EQUIPMENT: false });
  });
});
