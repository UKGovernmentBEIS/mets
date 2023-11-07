import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  VirApplicationRespondToRegulatorCommentsRequestTaskPayload,
} from 'pmrv-api';

import { mockStateRespond, mockVirApplicationRespondPayload } from './mock-vir-application-respond-payload';

export function mockStateBuild(value?: any): CommonTasksState {
  return {
    ...mockStateRespond,
    requestTaskItem: {
      ...mockStateRespond.requestTaskItem,
      requestTask: {
        ...mockStateRespond.requestTaskItem.requestTask,
        payload: {
          ...mockVirApplicationRespondPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export function mockPostBuild(
  value?: any,
  virRespondToRegulatorCommentsSectionsCompleted?: VirApplicationRespondToRegulatorCommentsRequestTaskPayload['virRespondToRegulatorCommentsSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
    requestTaskId: mockStateRespond.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
      ...value,
      virRespondToRegulatorCommentsSectionsCompleted: {
        ...mockVirApplicationRespondPayload.virRespondToRegulatorCommentsSectionsCompleted,
        ...virRespondToRegulatorCommentsSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
