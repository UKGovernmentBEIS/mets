import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  NERApplicationVerificationSubmitRequestTaskPayload,
  NERVerificationReport,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockNerSubmitPayload, nerCommonState } from '.';

export const nerMockVerificationPayload: NERApplicationVerificationSubmitRequestTaskPayload = {
  nerAttachments: mockNerSubmitPayload.nerAttachments,
  payloadType: 'NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
  ner: mockNerSubmitPayload.ner,
  verificationReport: {} as NERVerificationReport,
  verificationAttachments: { '11111111-1111-4111-a111-111111111111': 'testfile1.pdf' },
  verificationSectionsCompleted: {},
};

export const nerMockVerificationState = {
  requestTaskItem: {
    ...nerCommonState,
    allowedRequestTaskActions: ['NER_SAVE_APPLICATION_VERIFICATION', 'NER_VERIFICATION_UPLOAD_ATTACHMENT'],
    requestTask: {
      ...nerCommonState.requestTask,
      type: 'NER_APPLICATION_VERIFICATION_SUBMIT',
      payload: nerMockVerificationPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;

export function nerVerificationMockStateBuild(value?: any, status?: any, attachments?: any): CommonTasksState {
  return {
    ...nerMockVerificationState,
    isEditable: true,
    requestTaskItem: {
      ...nerMockVerificationState.requestTaskItem,
      requestTask: {
        ...nerMockVerificationState.requestTaskItem.requestTask,
        payload: {
          ...nerMockVerificationPayload,
          verificationSectionsCompleted: {
            ...nerMockVerificationPayload.verificationSectionsCompleted,
            ...status,
          },
          verificationReport: {
            ...nerMockVerificationPayload.verificationReport,
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

export function nerMockVerificationPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'NER_APPLICATION_SAVE_VERIFICATION',
    requestTaskId: nerMockVerificationState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'NER_APPLICATION_SAVE_VERIFICATION_PAYLOAD',
      ...nerMockVerificationPayload.verificationReport,
      ...value,
      verificationSectionsCompleted: {
        ...nerMockVerificationPayload.verificationSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
