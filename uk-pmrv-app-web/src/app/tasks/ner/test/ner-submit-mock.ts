import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  NerApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

const commonState = {
  requestInfo: {
    id: 'NER00122-4-v1',
    type: 'NER',
    competentAuthority: 'ENGLAND',
    accountId: 210,
    requestMetadata: {
      type: 'NER',
    },
  },
  requestTask: {
    id: 1,
    assignable: true,
    assigneeFullName: 'Operator name',
    assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
    startDate: '2025-10-13T11:23:36.521308Z',
  } as RequestTaskDTO,
};

export const mockNerSubmitPayload: NerApplicationSubmitRequestTaskPayload = {
  payloadType: 'NER_APPLICATION_SUBMIT_PAYLOAD',
  ner: {
    nerFiles: undefined,
    mmpFiles: undefined,
    notes: undefined,
  },
  nerSectionsCompleted: {},
  nerAttachments: {},
  nerFileVersion: undefined,
};

export const nerSubmitMockState = {
  requestTaskItem: {
    ...commonState,
    allowedRequestTaskActions: [],
    requestTask: {
      ...commonState.requestTask,
      type: 'NER_APPLICATION_SUBMIT',
      payload: mockNerSubmitPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockNerPostBuild(
  value?: any,
  nerSectionsCompleted?: NerApplicationSubmitRequestTaskPayload['nerSectionsCompleted'],
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] = 'NER_SAVE_APPLICATION',
  payloadType: RequestTaskActionPayload['payloadType'] = 'NER_SAVE_APPLICATION_PAYLOAD',
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType,
    requestTaskId: nerSubmitMockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType,
      ...value,
      nerSectionsCompleted: {
        ...mockNerSubmitPayload.nerSectionsCompleted,
        ...nerSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockNerSubmitStateBuild(value?: any): CommonTasksState {
  return {
    ...nerSubmitMockState,
    requestTaskItem: {
      ...nerSubmitMockState.requestTaskItem,
      requestTask: {
        ...nerSubmitMockState.requestTaskItem.requestTask,
        payload: {
          ...nerSubmitMockState,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}
