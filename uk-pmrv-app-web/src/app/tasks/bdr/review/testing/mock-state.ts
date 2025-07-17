import { mockVerificationApplyPayload } from '@tasks/bdr/verification-submit/testing/mock-bdr-payload';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const mockReview: BDRApplicationRegulatorReviewSubmitRequestTaskPayload = {
  bdrAttachments: mockVerificationApplyPayload.bdrAttachments,
  payloadType: 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
  bdr: mockVerificationApplyPayload.bdr,
  verificationReport: mockVerificationApplyPayload.verificationReport,
  regulatorReviewOutcome: null,
  regulatorReviewGroupDecisions: {},
  regulatorReviewSectionsCompleted: {},
  regulatorReviewAttachments: {},
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: [
      'BDR_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
      'BDR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
      'BDR_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      'BDR_REGULATOR_REVIEW_SAVE',
      'BDR_REGULATOR_REVIEW_SUBMIT',
    ],
    requestInfo: {
      id: 'BDR00182-2025',
      type: 'BDR',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      requestMetadata: {
        type: 'BDR',
        year: '2025',
        overallAssessmentType: 'VERIFIED_AS_SATISFACTORY',
      },
    },
    requestTask: {
      id: 1,
      type: 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT',
      payload: mockReview,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
