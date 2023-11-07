import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  VirApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { mockState, mockVirApplicationSubmitPayload } from './mock-vir-application-submit-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockState,
    requestTaskItem: {
      ...mockState.requestTaskItem,
      requestTask: {
        ...mockState.requestTaskItem.requestTask,
        payload: {
          ...mockVirApplicationSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  virSectionCompleted?: VirApplicationSubmitRequestTaskPayload['virSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'VIR_SAVE_APPLICATION',
    requestTaskId: mockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'VIR_SAVE_APPLICATION_PAYLOAD',
      ...value,
      virSectionsCompleted: {
        ...mockVirApplicationSubmitPayload.virSectionsCompleted,
        ...virSectionCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
