import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockAirApplicationRespondPayload, mockStateRespond } from './mock-air-application-respond-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockStateRespond,
    requestTaskItem: {
      ...mockStateRespond.requestTaskItem,
      requestTask: {
        ...mockStateRespond.requestTaskItem.requestTask,
        payload: {
          ...mockAirApplicationRespondPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  airRespondToRegulatorCommentsSectionsCompleted?: AirApplicationRespondToRegulatorCommentsRequestTaskPayload['airRespondToRegulatorCommentsSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
    requestTaskId: mockStateRespond.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
      ...value,
      airRespondToRegulatorCommentsSectionsCompleted: {
        ...mockAirApplicationRespondPayload.airRespondToRegulatorCommentsSectionsCompleted,
        ...airRespondToRegulatorCommentsSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
