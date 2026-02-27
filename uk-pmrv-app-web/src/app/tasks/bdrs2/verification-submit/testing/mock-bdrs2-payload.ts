import { mockBDRS2ApplicationSubmitPayloadCompleted } from '@tasks/bdrs2/submit/testing/mock-bdrs2-payload';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload, BDRS2VerificationReport } from 'pmrv-api';

export const mockVerificationApplyPayload: BDRS2ApplicationVerificationSubmitRequestTaskPayload = {
  bdrs2Attachments: mockBDRS2ApplicationSubmitPayloadCompleted.bdrs2Attachments,
  payloadType: 'BDRS2_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
  bdrs2: mockBDRS2ApplicationSubmitPayloadCompleted.bdrs2,
  verificationReport: {} as BDRS2VerificationReport,
  verificationAttachments: { '11111111-1111-4111-a111-111111111111': 'testfile1.pdf' },
  verificationSectionsCompleted: {},
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['BDRS2_SAVE_APPLICATION_VERIFICATION', 'BDRS2_VERIFICATION_UPLOAD_ATTACHMENT'],
    requestInfo: {
      id: 'BDRS2-00046-2026',
      type: 'BDRS2',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      requestMetadata: {
        type: 'BDRS2',
        year: '2026',
      },
    },
    requestTask: {
      id: 1,
      type: 'BDRS2_APPLICATION_VERIFICATION_SUBMIT',
      payload: mockVerificationApplyPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
