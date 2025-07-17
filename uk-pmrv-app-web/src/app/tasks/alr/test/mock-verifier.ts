import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  ALRApplicationVerificationSubmitRequestTaskPayload,
  ALRVerificationReport,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockALRApplicationSubmitPayloadCompleted } from './mock';

export const alrMockVerificationApplyPayload: ALRApplicationVerificationSubmitRequestTaskPayload = {
  alrAttachments: mockALRApplicationSubmitPayloadCompleted.alrAttachments,
  payloadType: 'ALR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
  alr: mockALRApplicationSubmitPayloadCompleted.alr,
  verificationReport: {} as ALRVerificationReport,
  verificationAttachments: { '11111111-1111-4111-a111-111111111111': 'testfile1.pdf' },
  verificationSectionsCompleted: {},
};

export const alrMockVerificationState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['ALR_SAVE_APPLICATION_VERIFICATION', 'ALR_VERIFICATION_UPLOAD_ATTACHMENT'],
    requestInfo: {
      id: 'ALR00189-2021',
      type: 'ALR',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      requestMetadata: {
        type: 'ALR',
        year: '2021',
      },
    },
    requestTask: {
      id: 1,
      type: 'ALR_APPLICATION_VERIFICATION_SUBMIT',
      payload: alrMockVerificationApplyPayload,
      assignable: true,
      assigneeFullName: 'Operator1 England',
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

export function alrVerificationMockStateBuild(value?: any, status?: any, attachments?: any): CommonTasksState {
  return {
    ...alrMockVerificationState,
    isEditable: true,
    requestTaskItem: {
      ...alrMockVerificationState.requestTaskItem,
      requestTask: {
        ...alrMockVerificationState.requestTaskItem.requestTask,
        payload: {
          ...alrMockVerificationApplyPayload,
          verificationSectionsCompleted: {
            ...alrMockVerificationApplyPayload.verificationSectionsCompleted,
            ...status,
          },
          verificationReport: {
            ...alrMockVerificationApplyPayload.verificationReport,
            ...value,
          },
          verificationAttachments: {
            ...attachments,
          },
        },
      },
    },
  } as CommonTasksState;
}

export function alrMockVerificationPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'ALR_SAVE_APPLICATION_VERIFICATION',
    requestTaskId: alrMockVerificationState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'ALR_APPLICATION_SAVE_VERIFICATION_PAYLOAD',
      ...alrMockVerificationApplyPayload.verificationReport,
      ...value,
      verificationSectionsCompleted: {
        ...alrMockVerificationApplyPayload.verificationSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
