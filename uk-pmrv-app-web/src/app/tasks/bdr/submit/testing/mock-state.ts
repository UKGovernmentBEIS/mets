import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  BDRApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockBDRApplicationSubmitPayload, mockBdrState } from './mock-bdr-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockBdrState,
    requestTaskItem: {
      ...mockBdrState.requestTaskItem,
      requestTask: {
        ...mockBdrState.requestTaskItem.requestTask,
        payload: {
          ...mockBDRApplicationSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  bdrSectionCompleted?: BDRApplicationSubmitRequestTaskPayload['bdrSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'BDR_SAVE_APPLICATION',
    requestTaskId: mockBdrState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'BDR_APPLICATION_SAVE_PAYLOAD',
      ...value,
      bdrSectionsCompleted: {
        ...mockBDRApplicationSubmitPayload.bdrSectionsCompleted,
        ...bdrSectionCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
