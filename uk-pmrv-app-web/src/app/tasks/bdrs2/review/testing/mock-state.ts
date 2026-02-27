import { mockVerificationApplyPayload } from '@tasks/bdrs2/verification-submit/testing/mock-bdrs2-payload';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const mockReview: BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload = {
  bdrs2Attachments: mockVerificationApplyPayload.bdrs2Attachments,
  payloadType: 'BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
  bdrs2: mockVerificationApplyPayload.bdrs2,
  verificationReport: mockVerificationApplyPayload.verificationReport,
  regulatorReviewOutcome: null,
  regulatorReviewGroupDecisions: {},
  regulatorReviewSectionsCompleted: {},
  regulatorReviewAttachments: {},
  bdrs2FileVersion: 1,
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: [
      'BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
      'BDRS2_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
      'BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS',
      'BDRS2_REGULATOR_REVIEW_SAVE',
      'BDRS2_REGULATOR_REVIEW_SUBMIT',
    ],
    requestInfo: {
      id: 'BDRS2-00046-2026',
      type: 'BDRS2',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      requestMetadata: {
        type: 'BDRS2',
        year: '2026',
        overallAssessmentType: 'VERIFIED_AS_SATISFACTORY',
      },
    },
    requestTask: {
      id: 1,
      type: 'BDRS2_APPLICATION_REGULATOR_REVIEW_SUBMIT',
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
