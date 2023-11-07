import {
  Doal,
  DoalApplicationSubmitRequestTaskPayload,
  DoalProceedToAuthorityDetermination,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'pmrv-api';

import { DoalAuthorityTaskSectionKey, DoalTaskSectionKey } from '../core/doal-task.type';

export const doalCompleted: Doal = {
  operatorActivityLevelReport: {
    document: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
    areActivityLevelsEstimated: false,
    comment: 'operatorActivityLevelReport',
  },
  verificationReportOfTheActivityLevelReport: {
    document: 'abf68262-f6d1-4137-b654-c3302079d023',
    comment: 'verificationReportOfTheActivityLevelReportComment',
  },
  additionalDocuments: {
    exist: true,
    documents: ['7e2036b4-c857-4caa-afef-97e690df3454'],
    comment: 'additionalDocumentsComment',
  },
  activityLevelChangeInformation: {
    activityLevels: [
      {
        year: 2025,
        subInstallationName: 'ADIPIC_ACID',
        changeType: 'INCREASE',
        changedActivityLevel: 'changedActivityLevel',
        comments: 'activityLevel1Comment',
      },
    ],
    areConservativeEstimates: true,
    explainEstimates: 'explainEstimates',
    preliminaryAllocations: [
      {
        subInstallationName: 'ALUMINIUM',
        year: 2025,
        allowances: 10,
      },
    ],
    commentsForUkEtsAuthority: 'commentsForUkEtsAuthorityComment',
  },
  determination: {
    type: 'PROCEED_TO_AUTHORITY',
    reason: 'reason',
    articleReasonGroupType: 'ARTICLE_6A_REASONS',
    articleReasonItems: ['ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5'],
    hasWithholdingOfAllowances: true,
    withholdingAllowancesNotice: {
      noticeIssuedDate: '2022-08-10',
      withholdingOfAllowancesComment: 'withholdingOfAllowancesComment',
    },
    needsOfficialNotice: true,
  } as DoalProceedToAuthorityDetermination,
};

export const mockDoalApplicationSubmitRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: [
    'DOAL_SAVE_APPLICATION',
    'DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION',
    'DOAL_CLOSE_APPLICATION',
    'DOAL_PROCEED_TO_AUTHORITY_APPLICATION',
    'DOAL_REQUEST_PEER_REVIEW',
  ],
  requestTask: {
    id: 1,
    type: 'DOAL_APPLICATION_SUBMIT',
    payload: {
      payloadType: 'DOAL_APPLICATION_SUBMIT_PAYLOAD',
      doal: doalCompleted,
      doalSectionsCompleted: {
        operatorActivityLevelReport: true,
        verificationReportOfTheActivityLevelReport: true,
        additionalDocuments: true,
        activityLevelChangeInformation: true,
        determination: true,
      } as { [key in DoalTaskSectionKey]: boolean },
      doalAttachments: {
        '2b587c89-1973-42ba-9682-b3ea5453b9dd': '1.png',
        'abf68262-f6d1-4137-b654-c3302079d023': '2.png',
        '7e2036b4-c857-4caa-afef-97e690df3454': '3.png',
      },
      historicalActivityLevels: [
        {
          year: 2026,
          subInstallationName: 'DISTRICT_HEATING',
          changeType: 'REGULATOR_REJECTS_ADJUSTMENT',
          changedActivityLevel: 'changedActivityLevel',
          comments: 'activityLevel1Comment',
          creationDate: '2022-11-29T12:12:48.469862Z',
        },
      ],
      historicalPreliminaryAllocations: [
        {
          subInstallationName: 'PLASTER',
          year: 2030,
          allowances: 12,
        },
      ],
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    id: 'DOAL12345-2025-1',
    requestMetadata: {
      type: 'DOAL',
      year: '2025',
    } as any,
    type: 'DOAL',
    competentAuthority: 'ENGLAND',
    accountId: 22,
  },
};

export const mockDoalAuthorityResponseRequestTaskTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: ['DOAL_SAVE_AUTHORITY_RESPONSE', 'DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION'],
  requestTask: {
    id: 1,
    type: 'DOAL_AUTHORITY_RESPONSE',
    payload: {
      payloadType: 'DOAL_AUTHORITY_RESPONSE_PAYLOAD',
      doalAuthority: {
        dateSubmittedToAuthority: {
          date: new Date('2023-03-12'),
        },
        authorityResponse: {
          type: 'VALID_WITH_CORRECTIONS',
          authorityRespondDate: new Date('2023-03-12'),
          decisionNotice: 'Decision notice',
          preliminaryAllocations: [
            {
              year: 2024,
              allowances: 200,
              subInstallationName: 'ALUMINIUM',
            },
            {
              year: 2023,
              allowances: 100,
              subInstallationName: 'ALUMINIUM',
            },
          ],
        },
      },
      doalSectionsCompleted: {
        dateSubmittedToAuthority: true,
        authorityResponse: true,
      } as { [key in DoalAuthorityTaskSectionKey]: boolean },
      doalAttachments: {},
      regulatorPreliminaryAllocations: [
        {
          subInstallationName: 'PLASTER',
          year: 2030,
          allowances: 12,
        },
      ],
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    id: 'DOAL12345-2025-1',
    requestMetadata: {
      type: 'DOAL',
      year: '2025',
    } as any,
    type: 'DOAL',
    competentAuthority: 'ENGLAND',
    accountId: 22,
  },
};

const mockDoalSubmitCompleted: DoalApplicationSubmitRequestTaskPayload = {
  doal: {
    operatorActivityLevelReport: {
      document: '11111111-1111-4111-a111-111111111111',
      areActivityLevelsEstimated: true,
      comment: 'information submitted 1',
    },
    verificationReportOfTheActivityLevelReport: {
      document: '22222222-2222-4222-a222-222222222222',
      comment: 'information submitted 2',
    },
    additionalDocuments: {
      exist: false,
      comment: 'information submitted 3',
    },
    activityLevelChangeInformation: {
      activityLevels: [
        {
          year: 2022,
          subInstallationName: 'ADIPIC_ACID',
          changeType: 'CESSATION',
          changedActivityLevel: '20',
          comments: 'Comments on this change',
        },
      ],
      areConservativeEstimates: true,
      explainEstimates: 'Explanation for estimation',
      preliminaryAllocations: [
        {
          subInstallationName: 'ALUMINIUM',
          year: 2022,
          allowances: 10,
        },
      ],
      commentsForUkEtsAuthority: 'large changes to activity levels or allocation',
    },
    determination: {
      type: 'PROCEED_TO_AUTHORITY',
      reason: 'Official notice',
      articleReasonGroupType: 'ARTICLE_6A_REASONS',
      articleReasonItems: ['ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5'],
      hasWithholdingOfAllowances: false,
      needsOfficialNotice: false,
    } as DoalProceedToAuthorityDetermination,
  },
  doalSectionsCompleted: {
    determination: true,
    additionalDocuments: true,
    operatorActivityLevelReport: true,
    activityLevelChangeInformation: true,
    verificationReportOfTheActivityLevelReport: true,
  },
  doalAttachments: {
    '11111111-1111-4111-a111-111111111111': '100.bmp',
    '22222222-2222-4222-a222-222222222222': '200.png',
  },
  historicalPreliminaryAllocations: [],
  historicalActivityLevels: [],
};

export const mockDoalPeerWaitReviewResponseRequestTaskTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: ['DOAL_CANCEL_APPLICATION'],
  requestTask: {
    id: 1,
    type: 'DOAL_WAIT_FOR_PEER_REVIEW',
    assignable: true,
    assigneeFullName: 'Regulator1 England',
    assigneeUserId: '252cd19d-b2f4-4e6b-ba32-a225d0777c98',
    startDate: '2023-01-01T00:00:00.00000Z',
    payload: {
      payloadType: 'DOAL_WAIT_FOR_PEER_REVIEW_PAYLOAD',
      ...mockDoalSubmitCompleted,
    } as RequestTaskPayload,
  },
  requestInfo: {
    id: 'DOAL12345-2025-1',
    accountId: 22,
    competentAuthority: 'ENGLAND',
    type: 'DOAL',
    requestMetadata: {
      type: 'DOAL',
      year: '2023',
    } as any,
  },
};

export const mockDoalPeerReviewResponseRequestTaskTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: ['DOAL_SUBMIT_PEER_REVIEW_DECISION'],
  requestTask: {
    id: 1,
    type: 'DOAL_APPLICATION_PEER_REVIEW',
    assignable: true,
    assigneeFullName: 'Regulator1 England',
    assigneeUserId: '252cd19d-b2f4-4e6b-ba32-a225d0777c98',
    startDate: '2023-01-01T00:00:00.00000Z',
    payload: {
      payloadType: 'DOAL_APPLICATION_PEER_REVIEW_PAYLOAD',
      ...mockDoalSubmitCompleted,
    } as RequestTaskPayload,
  },
  requestInfo: {
    id: 'DOAL12345-2025-1',
    accountId: 22,
    competentAuthority: 'ENGLAND',
    type: 'DOAL',
    requestMetadata: {
      type: 'DOAL',
      year: '2023',
    } as any,
  },
};

export function updateMockDoalApplicationSubmitRequestTaskItem(
  doalPart: Partial<Doal>,
  doalSectionsCompleted?: { [key: string]: boolean },
): RequestTaskItemDTO {
  return {
    ...mockDoalApplicationSubmitRequestTaskItem,
    requestTask: {
      ...mockDoalApplicationSubmitRequestTaskItem.requestTask,
      payload: {
        ...mockDoalApplicationSubmitRequestTaskItem.requestTask.payload,
        doal: {
          ...(mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doal,
          ...doalPart,
        },
        doalSectionsCompleted:
          doalSectionsCompleted ??
          (mockDoalApplicationSubmitRequestTaskItem.requestTask.payload as any).doalSectionsCompleted,
      } as RequestTaskPayload,
    },
  };
}
