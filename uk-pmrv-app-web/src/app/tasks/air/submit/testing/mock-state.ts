import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AirApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockAirApplicationSubmitPayload, mockState } from './mock-air-application-submit-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockState,
    requestTaskItem: {
      ...mockState.requestTaskItem,
      requestTask: {
        ...mockState.requestTaskItem.requestTask,
        payload: {
          ...mockAirApplicationSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  airSectionCompleted?: AirApplicationSubmitRequestTaskPayload['airSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'AIR_SAVE_APPLICATION',
    requestTaskId: mockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'AIR_SAVE_APPLICATION_PAYLOAD',
      ...value,
      airSectionsCompleted: {
        ...mockAirApplicationSubmitPayload.airSectionsCompleted,
        ...airSectionCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
