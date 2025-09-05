import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockHSETIApplicationSubmitPayloadCompleted } from './mock';

export const hsetiMockReviewApplyPayload: HSETIApplicationRegulatorReviewSubmitRequestTaskPayload = {
  hsetiAttachments: mockHSETIApplicationSubmitPayloadCompleted.hsetiAttachments,
  payloadType: 'HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
  hseti: mockHSETIApplicationSubmitPayloadCompleted.hseti,
  hsetiSectionsCompleted: {
    details: true,
    sendReport: true,
    OVERALL_DECISION: false,
    HSETI: false,
  },
};

export const hsetiMockReviewState = {
  requestTaskItem: {
    allowedRequestTaskActions: [
      'HSE_TI_REGULATOR_REVIEW_SAVE',
      'HSE_TI_UPLOAD_ATTACHMENT',
      'HSE_TI_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
    ],
    requestInfo: {
      id: 'HSETI00035-2021_2025',
      type: 'HSE_TI',
      competentAuthority: 'ENGLAND',
      accountId: 35,
      requestMetadata: {
        type: 'HSE_TI',
        allocationPeriod: 'PERIOD_2021_2025',
      },
    },
    requestTask: {
      id: 1,
      type: 'HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT',
      payload: hsetiMockReviewApplyPayload,
      assignable: true,
      assigneeFullName: 'Regulator England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      daysRemaining: -270,
      startDate: '2023-03-15T15:04:23.866188Z',
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockHsetiReviewPostBuild(
  value?: any,
  hsetiSectionsCompleted?: HSETIApplicationRegulatorReviewSubmitRequestTaskPayload['hsetiSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'HSE_TI_REGULATOR_REVIEW_SAVE',
    requestTaskId: hsetiMockReviewState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'HSE_TI_APPLICATION_SAVE_PAYLOAD',
      ...value,
      hsetiSectionsCompleted: {
        ...hsetiMockReviewApplyPayload.hsetiSectionsCompleted,
        ...hsetiSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}
export function mockHsetiReviewStateBuild(value?: any): CommonTasksState {
  return {
    ...hsetiMockReviewState,
    requestTaskItem: {
      ...hsetiMockReviewState.requestTaskItem,
      requestTask: {
        ...hsetiMockReviewState.requestTaskItem.requestTask,
        payload: {
          ...hsetiMockReviewState,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}
