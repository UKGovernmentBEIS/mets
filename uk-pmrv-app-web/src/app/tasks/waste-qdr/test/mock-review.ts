import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
} from 'pmrv-api';

export const wasteQdrMockReviewPayload = {
  payloadType: 'WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
  sendEmailNotification: true,
  qdr: {
    reportProvided: false,
    reasonForUnprovided: 'sfasdfa',
  },
} as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;

export const wasteQdrMockReviewState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['WASTE_QDR_SAVE_REVIEW_GROUP_DECISION'],
    requestInfo: {
      id: 'WQDR00005-2025-Q3',
      type: 'WASTE_QDR',
      competentAuthority: 'ENGLAND',
      accountId: 5,
      requestMetadata: {
        type: 'WASTE_QDR',
        year: '2025',
        quarter: 'Q3',
      },
    },
    requestTask: {
      id: 1,
      type: 'WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT',
      payload: wasteQdrMockReviewPayload,
      assignable: true,
      assigneeUserId: '80a57c50-1aaa-421f-9e1d-fdf3268cca8b',
      assigneeFullName: 'Regulator England',
      startDate: '2025-11-14T16:36:45.309737Z',
      daysRemaining: -270,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function mockWasteQdrReviewStateBuild(value?: any): CommonTasksState {
  return {
    ...wasteQdrMockReviewState,
    requestTaskItem: {
      ...wasteQdrMockReviewState.requestTaskItem,
      requestTask: {
        ...wasteQdrMockReviewState.requestTaskItem.requestTask,
        payload: {
          ...wasteQdrMockReviewPayload,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}

export const mockWasteQdrReviewPostBuild = (
  value?: any,
  regulatorReviewSectionsCompleted?: WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload['regulatorReviewSectionsCompleted'],
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] = 'WASTE_QDR_SAVE_APPLICATION',
  payloadType: RequestTaskActionPayload['payloadType'] = 'WASTE_QDR_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
): RequestTaskActionProcessDTO => {
  return {
    requestTaskActionType,
    requestTaskId: wasteQdrMockReviewState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType,
      ...value,
      regulatorReviewSectionsCompleted: {
        ...wasteQdrMockReviewPayload.wasteQDRSectionsCompleted,
        ...regulatorReviewSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
};
