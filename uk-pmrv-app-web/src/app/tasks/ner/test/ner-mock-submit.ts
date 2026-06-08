import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  NerApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { nerCommonState } from '.';

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

export const mockNerSubmitPayloadCompleted: NerApplicationSubmitRequestTaskPayload = {
  payloadType: 'NER_APPLICATION_SUBMIT_PAYLOAD',
  ner: {
    nerFiles: { file: 'aa1', supportingFiles: ['aa2'] },
    mmpFiles: { file: 'aa1', supportingFiles: ['aa2'] },
    notes: 'test',
  },
  nerSectionsCompleted: { NER: true },
  nerAttachments: {},
  nerFileVersion: undefined,
};

export const nerSubmitMockState = {
  requestTaskItem: {
    ...nerCommonState,
    allowedRequestTaskActions: [],
    requestTask: {
      ...nerCommonState.requestTask,
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
