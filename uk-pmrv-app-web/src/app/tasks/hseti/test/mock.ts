import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  HSETIApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

export const mockHSEApplicationSubmitPayload: HSETIApplicationSubmitRequestTaskPayload = {
  payloadType: 'HSE_TI_APPLICATION_SUBMIT_PAYLOAD',
  hsetiSectionsCompleted: {
    details: true,
  },
};

export const mockHSEApplicationSubmitPayloadCompleted: HSETIApplicationSubmitRequestTaskPayload = {
  payloadType: 'HSE_TI_APPLICATION_SUBMIT_PAYLOAD',
  hseti: {
    files: ['b6c0615d-cb16-473e-9fe0-d3fa6991e4cf'],
    hsetiFile: 'b6c0615d-cb16-473e-9fe0-d3fa6991e4cf',
  },
  hsetiAttachments: { 'b6c0615d-cb16-473e-9fe0-d3fa6991e4cf': 'test.PNG' },
  hsetiSectionsCompleted: {
    details: true,
  },
};

export const mockHseTiState = {
  requestTaskItem: {
    allowedRequestTaskActions: [],
    requestInfo: {
      id: 'HSE_TI00005-2021',
      type: 'HSE_TI',
      competentAuthority: 'ENGLAND',
      accountId: 5,
      requestMetadata: {
        type: 'HSE_TI',
        allocationPeriod: 'PERIOD_2021_2025',
      },
    },
    requestTask: {
      id: 1,
      type: 'HSE_TI_APPLICATION_SUBMIT',
      assignable: true,
      assigneeFullName: 'Operator1 England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      startDate: '2023-03-15T15:04:23.866188Z',
      payload: mockHSEApplicationSubmitPayload,
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockHseTiState,
    requestTaskItem: {
      ...mockHseTiState.requestTaskItem,
      requestTask: {
        ...mockHseTiState.requestTaskItem.requestTask,
        payload: {
          ...mockHSEApplicationSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  hsetiSectionsCompleted?: HSETIApplicationSubmitRequestTaskPayload['hsetiSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'HSE_TI_SAVE_APPLICATION',
    requestTaskId: mockHseTiState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'HSE_TI_APPLICATION_SAVE_PAYLOAD',
      ...value,
      hsetiSectionsCompleted: {
        ...mockHSEApplicationSubmitPayload.hsetiSectionsCompleted,
        ...hsetiSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}

export const mockHSETIApplicationSubmitPayloadCompleted: HSETIApplicationSubmitRequestTaskPayload = {
  payloadType: 'HSE_TI_APPLICATION_SUBMIT_PAYLOAD',
  hseti: {
    hsetiFile: '119f3261-69b2-435d-bb19-4545809c3864',
    files: ['119f3261-69b2-435d-bb19-4545809c3864'],
  },
  hsetiAttachments: {
    '119f3261-69b2-435d-bb19-4545809c3864': 'test.pdf',
  },
  hsetiSectionsCompleted: { details: true },
};
