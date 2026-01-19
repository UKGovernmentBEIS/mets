import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  BDRS2ApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockBDRS2ApplicationSubmitPayload, mockBdrS2State } from './mock-bdrs2-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockBdrS2State,
    requestTaskItem: {
      ...mockBdrS2State.requestTaskItem,
      requestTask: {
        ...mockBdrS2State.requestTaskItem.requestTask,
        payload: {
          ...mockBDRS2ApplicationSubmitPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  bdrs2SectionsCompleted?: BDRS2ApplicationSubmitRequestTaskPayload['bdrs2SectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'BDRS2_SAVE_APPLICATION',
    requestTaskId: mockBdrS2State.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'BDRS2_APPLICATION_SAVE_PAYLOAD',
      ...value,
      bdrs2SectionsCompleted: {
        ...mockBDRS2ApplicationSubmitPayload.bdrs2SectionsCompleted,
        ...bdrs2SectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
