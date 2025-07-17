import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  ALRApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

const commonState = {
  requestInfo: {
    id: 'ALR00107-2022-1',
    type: 'ALR',
    competentAuthority: 'ENGLAND',
    accountId: 210,
    requestMetadata: {
      type: 'ALR',
      year: '2022',
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

export const mockAlrSubmitPayload: ALRApplicationSubmitRequestTaskPayload = {
  payloadType: 'ALR_SUBMIT_PAYLOAD',
  alrSectionsCompleted: { activity: true },
};

export const mockALRApplicationSubmitPayloadCompleted: ALRApplicationSubmitRequestTaskPayload = {
  payloadType: 'ALR_SUBMIT_PAYLOAD',
  alr: {
    alrFile: '119f3261-69b2-435d-bb19-4545809c3864',
    files: ['119f3261-69b2-435d-bb19-4545809c3864'],
  },
  alrAttachments: {
    '119f3261-69b2-435d-bb19-4545809c3864': 'test.pdf',
  },
  alrSectionsCompleted: { activity: true },
};

export const alrSubmitMockState = {
  requestTaskItem: {
    ...commonState,
    allowedRequestTaskActions: [],
    requestTask: {
      ...commonState.requestTask,
      type: 'ALR_APPLICATION_SUBMIT',
      payload: mockAlrSubmitPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockAlrStateBuild(value?: any): CommonTasksState {
  return {
    ...alrSubmitMockState,
    requestTaskItem: {
      ...alrSubmitMockState.requestTaskItem,
      requestTask: {
        ...alrSubmitMockState.requestTaskItem.requestTask,
        payload: {
          ...mockAlrSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockAlrPostBuild(
  value?: any,
  alrSectionsCompleted?: ALRApplicationSubmitRequestTaskPayload['alrSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'ALR_SAVE_APPLICATION',
    requestTaskId: alrSubmitMockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'ALR_APPLICATION_SAVE_PAYLOAD',
      ...value,
      alrSectionsCompleted: {
        ...mockAlrSubmitPayload.alrSectionsCompleted,
        ...alrSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
