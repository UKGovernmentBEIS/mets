import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  BDRS2ApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

const commonState = {
  requestInfo: {
    id: 'BDRS2-00001-2026',
    type: 'BDRS2',
    competentAuthority: 'ENGLAND',
    accountId: 210,
    requestMetadata: {
      type: 'BDRS2',
      year: '2026',
    },
  },
  requestTask: {
    id: 1,
    assignable: true,
    assigneeFullName: 'Regulator1 England',
    assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
    startDate: '2023-03-15T15:04:23.866188Z',
  } as RequestTaskDTO,
};

export const mockBdrs2SubmitPayload: BDRS2ApplicationSubmitRequestTaskPayload = {
  payloadType: 'BDRS2_APPLICATION_SUBMIT_PAYLOAD',
  bdrs2SectionsCompleted: { baseline: false },
};

export const bdrs2SubmitMockState = {
  requestTaskItem: {
    ...commonState,
    allowedRequestTaskActions: [],
    requestTask: {
      ...commonState.requestTask,
      type: 'BDRS2_APPLICATION_SUBMIT',
      payload: mockBdrs2SubmitPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockBdrs2StateBuild(value?: any): CommonTasksState {
  return {
    ...bdrs2SubmitMockState,
    requestTaskItem: {
      ...bdrs2SubmitMockState.requestTaskItem,
      requestTask: {
        ...bdrs2SubmitMockState.requestTaskItem.requestTask,
        payload: {
          ...mockBdrs2SubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockBdrs2PostBuild(
  value?: any,
  bdrs2SectionsCompleted?: BDRS2ApplicationSubmitRequestTaskPayload['bdrs2SectionsCompleted'],
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] = 'BDRS2_SAVE_APPLICATION',
  payloadType: RequestTaskActionPayload['payloadType'] = 'BDRS2_APPLICATION_SAVE_PAYLOAD',
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType,
    requestTaskId: bdrs2SubmitMockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType,
      ...value,
      bdrs2SectionsCompleted: {
        ...mockBdrs2SubmitPayload.bdrs2SectionsCompleted,
        ...bdrs2SectionsCompleted,
      },
      bdrs2FileVersion: 1,
    } as RequestTaskActionPayload,
  };
}
