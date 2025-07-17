import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  PermanentCessationApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import {
  mockPermanentCessationApplicationSubmitPayload,
  mockPermanentCessationState,
} from './mock-permanent-cessation-payload';

export function permanentCessationMockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockPermanentCessationState,
    requestTaskItem: {
      ...mockPermanentCessationState.requestTaskItem,
      requestTask: {
        ...mockPermanentCessationState.requestTaskItem.requestTask,
        payload: {
          ...mockPermanentCessationApplicationSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function permanentCessationMockPostBuild(
  value?: any,
  permanentCessationSectionsCompleted?: PermanentCessationApplicationSubmitRequestTaskPayload['permanentCessationSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMANENT_CESSATION_SAVE_APPLICATION',
    requestTaskId: mockPermanentCessationState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'PERMANENT_CESSATION_SUBMIT_PAYLOAD',
      ...value,
      permanentCessationSectionsCompleted: {
        ...mockPermanentCessationApplicationSubmitPayload.permanentCessationSectionsCompleted,
        ...permanentCessationSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
