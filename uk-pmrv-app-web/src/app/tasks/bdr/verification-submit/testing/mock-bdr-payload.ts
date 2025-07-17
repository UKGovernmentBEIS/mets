import { mockBDRApplicationSubmitPayloadCompleted } from '@tasks/bdr/submit/testing/mock-bdr-payload';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { BDRApplicationVerificationSubmitRequestTaskPayload, BDRVerificationReport } from 'pmrv-api';

export const mockVerificationApplyPayload: BDRApplicationVerificationSubmitRequestTaskPayload = {
  bdrAttachments: mockBDRApplicationSubmitPayloadCompleted.bdrAttachments,
  payloadType: 'BDR_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD',
  bdr: mockBDRApplicationSubmitPayloadCompleted.bdr,
  verificationReport: {} as BDRVerificationReport,
  verificationAttachments: { '11111111-1111-4111-a111-111111111111': 'testfile1.pdf' },
  verificationSectionsCompleted: {},
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['BDR_SAVE_APPLICATION_VERIFICATION', 'BDR_VERIFICATION_UPLOAD_ATTACHMENT'],
    requestInfo: {
      id: 'BDR00180-2025',
      type: 'BDR',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      requestMetadata: {
        type: 'BDR',
        year: '2025',
      },
    },
    requestTask: {
      id: 1,
      type: 'BDR_APPLICATION_VERIFICATION_SUBMIT',
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
