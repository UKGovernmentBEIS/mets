import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  WasteQDRApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

const commonState = {
  requestInfo: {
    id: 'WQDR00126-2025-Q3',
    type: 'WASTE_QDR',
    competentAuthority: 'ENGLAND',
    accountId: 210,
    requestMetadata: {
      type: 'WASTE_QDR',
      year: '2025',
      quarter: 'Q3',
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

export const mockWasteQdrSubmitPayload: WasteQDRApplicationSubmitRequestTaskPayload = {
  payloadType: 'WASTE_QDR_SUBMIT_PAYLOAD',
  qdr: {
    reportProvided: null,
    reasonForUnprovided: null,
    report: null,
    supportingFiles: [],
    notes: null,
  },
};

export const wasteQdrSubmitMockState = {
  requestTaskItem: {
    ...commonState,
    allowedRequestTaskActions: [],
    requestTask: {
      ...commonState.requestTask,
      type: 'WASTE_QDR_APPLICATION_SUBMIT',
      payload: mockWasteQdrSubmitPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockWasteQdrPostBuild(
  value?: any,
  wasteQDRSectionsCompleted?: WasteQDRApplicationSubmitRequestTaskPayload['wasteQDRSectionsCompleted'],
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] = 'WASTE_QDR_SAVE_APPLICATION',
  payloadType: RequestTaskActionPayload['payloadType'] = 'WASTE_QDR_APPLICATION_SAVE_PAYLOAD',
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType,
    requestTaskId: wasteQdrSubmitMockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType,
      ...value,
      wasteQDRSectionsCompleted: {
        ...mockWasteQdrSubmitPayload.wasteQDRSectionsCompleted,
        ...wasteQDRSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockWasteQdrSubmitStateBuild(value?: any): CommonTasksState {
  return {
    ...wasteQdrSubmitMockState,
    requestTaskItem: {
      ...wasteQdrSubmitMockState.requestTaskItem,
      requestTask: {
        ...wasteQdrSubmitMockState.requestTaskItem.requestTask,
        payload: {
          ...mockWasteQdrSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}
