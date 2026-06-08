import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  NERApplicationRegulatorReviewSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { nerCommonState, nerMockVerificationPayload } from '.';

export const nerMockReviewPayload: NERApplicationRegulatorReviewSubmitRequestTaskPayload = {
  ...nerMockVerificationPayload,
  regulatorReviewSectionsCompleted: {},
  regulatorReviewAttachments: {},
  regulatorReviewOutcome: {},
};

export const nerReviewMockState = {
  requestTaskItem: {
    ...nerCommonState,
    allowedRequestTaskActions: [
      'NER_SAVE_APPLICATION_REVIEW',
      'NER_SAVE_REVIEW_GROUP_DECISION',
      'NER_SAVE_REVIEW_DETERMINATION',
      'NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
    ],
    requestTask: {
      ...nerCommonState.requestTask,
      type: 'NER_APPLICATION_REVIEW',
      payload: nerMockReviewPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockNerReviewPostBuild(
  value?: any,
  regulatorReviewSectionsCompleted?: NERApplicationRegulatorReviewSubmitRequestTaskPayload['regulatorReviewSectionsCompleted'],
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] = 'NER_SAVE_APPLICATION_REVIEW',
  payloadType: RequestTaskActionPayload['payloadType'] = 'NER_SAVE_APPLICATION_REVIEW_PAYLOAD',
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType,
    requestTaskId: nerReviewMockState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType,
      ...value,
      regulatorReviewSectionsCompleted: {
        ...nerMockReviewPayload.regulatorReviewSectionsCompleted,
        ...regulatorReviewSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockNerReviewStateBuild(value?: any): CommonTasksState {
  return {
    ...nerReviewMockState,
    requestTaskItem: {
      ...nerReviewMockState.requestTaskItem,
      requestTask: {
        ...nerReviewMockState.requestTaskItem.requestTask,
        payload: {
          ...nerReviewMockState,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}
