import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRVerificationReport,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { mockALRApplicationSubmitPayloadCompleted } from './mock';

export const alrMockReviewApplyPayload: ALRApplicationRegulatorReviewSubmitRequestTaskPayload = {
  alrAttachments: mockALRApplicationSubmitPayloadCompleted.alrAttachments,
  payloadType: 'ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
  alr: mockALRApplicationSubmitPayloadCompleted.alr,
  verificationReport: {
    opinionStatement: {
      notes: 'Note',
      opinionStatementFiles: ['fadd9348-1099-4d3c-a703-c45ec5eefdac'],
      supportingFiles: ['e5ae6d57-ac9e-4bf1-810f-7d832d47ce6e', '8718679e-488e-49e7-acce-eb409956b32e'],
    },
    overallAssessment: { reasons: 'Reasons', type: 'VERIFIED_WITH_COMMENTS' },
    verificationBodyDetails: {
      name: 'Verifyer',
      accreditationReferenceNumber: '12345',
      address: { line1: 'Line 1', city: 'City', country: 'BH', postcode: 'Postcode' },
      emissionTradingSchemes: ['CORSIA', 'EU_ETS_INSTALLATIONS', 'UK_ETS_AVIATION', 'UK_ETS_INSTALLATIONS'],
    },
  } as ALRVerificationReport,
  verificationAttachments: {
    '8718679e-488e-49e7-acce-eb409956b32e': 'testfile1.pdf',
    'e5ae6d57-ac9e-4bf1-810f-7d832d47ce6e': 'testfile2.pdf',
    'fadd9348-1099-4d3c-a703-c45ec5eefdac': 'testfile3.pdf',
  },
  verificationSectionsCompleted: {},
  regulatorReviewOutcome: {
    historicalActivityLevels: [
      {
        year: 2023,
        subInstallationName: 'ADIPIC_ACID',
        changeType: 'CESSATION',
        changedActivityLevel: '15.55',
        comments: 'comment',
        creationDate: '2025-07-02T18:55:22Z',
      },
    ],
    activityLevels: [
      {
        year: 2022,
        subInstallationName: 'DOLIME',
        changeType: 'REGULATOR_REJECTS_ADJUSTMENT',
        changedActivityLevel: '11.55',
        comments: 'Comments 1',
        activityLevelChangeId: '0',
      },
      {
        year: 2023,
        subInstallationName: 'FACING_BRICKS',
        changeType: 'CESSATION',
        changedActivityLevel: '43.33',
        comments: 'Comments 2',
        activityLevelChangeId: '1',
      },
    ],
    conservativeDeterminesActivity: false,
    ukEtsAuthorityComments: 'A comment',
    allocations: [
      {
        year: 2025,
        subInstallationName: 'ALUMINIUM',
        allowances: 10,
        allocationId: '0',
      },
    ],
    determination: undefined,
  },
  regulatorReviewAttachments: {},
  regulatorReviewSectionsCompleted: { ALC: false },
  regulatorReviewGroupDecisions: {},
};

export const alrMockReviewState = {
  requestTaskItem: {
    allowedRequestTaskActions: [
      'ALR_SAVE_APPLICATION_VERIFICATION',
      'ALR_VERIFICATION_UPLOAD_ATTACHMENT',
      'ALR_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
      'ALR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
      'ALR_REGULATOR_REVIEW_SAVE',
    ],
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
      type: 'ALR_APPLICATION_REGULATOR_REVIEW_SUBMIT',
      payload: alrMockReviewApplyPayload,
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

export function mockAlrReviewPostBuild(
  value?: any,
  regulatorReviewSectionsCompleted?: ALRApplicationRegulatorReviewSubmitRequestTaskPayload['regulatorReviewSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'ALR_REGULATOR_REVIEW_SAVE',
    requestTaskId: alrMockReviewState.requestTaskItem.requestTask.id,
    requestTaskActionPayload: {
      payloadType: 'ALR_REGULATOR_REVIEW_SAVE_PAYLOAD',
      ...value,
      regulatorReviewSectionsCompleted: {
        ...alrMockReviewApplyPayload.regulatorReviewSectionsCompleted,
        ...regulatorReviewSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockAlrReviewStateBuild(value?: any): CommonTasksState {
  return {
    ...alrMockReviewState,
    requestTaskItem: {
      ...alrMockReviewState.requestTaskItem,
      requestTask: {
        ...alrMockReviewState.requestTaskItem.requestTask,
        payload: {
          ...alrMockReviewState,
          ...value,
        },
      },
    },
  } as CommonTasksState;
}
