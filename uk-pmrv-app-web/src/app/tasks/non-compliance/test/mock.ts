import {
  NonComplianceApplicationSubmitRequestTaskPayload,
  NonComplianceCivilPenaltyRequestTaskPayload,
  NonComplianceDailyPenaltyNoticeRequestTaskPayload,
  NonComplianceFinalDeterminationRequestTaskPayload,
  NonComplianceNoticeOfIntentRequestTaskPayload,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'pmrv-api';

export const mockCompletedNonComplianceApplicationSubmitRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: ['NON_COMPLIANCE_SUBMIT_APPLICATION'],
  requestTask: {
    id: 1,
    type: 'NON_COMPLIANCE_APPLICATION_SUBMIT',
    payload: {
      payloadType: 'NON_COMPLIANCE_APPLICATION_SUBMIT_PAYLOAD',
      civilPenalty: true,
      comments: 'comments step 11',
      complianceDate: '2022-02-22',
      dailyPenalty: true,
      noticeOfIntent: true,
      nonComplianceDate: '2024-11-11',
      reason: 'FAILURE_TO_SURRENDER_ALLOWANCES',
      selectedRequests: ['AEM00094'],
      availableRequests: [{ id: 'AEM00094', type: 'INSTALLATION_ACCOUNT_OPENING' }],
      nonComplianceAttachments: { '2b587c89-1973-42ba-9682-b3ea5453b9dd': 'supportingDoc1.pdf' },
      sectionCompleted: true,
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    accountId: 22,
    competentAuthority: 'ENGLAND',
    id: 'DRE00022-2022-1',
    type: 'NON_COMPLIANCE',
  },
};

export function updateMockedNonCompliance(
  nonCompliancePart?: Partial<NonComplianceApplicationSubmitRequestTaskPayload>,
  sectionCompleted?: boolean,
): RequestTaskItemDTO {
  return {
    ...mockCompletedNonComplianceApplicationSubmitRequestTaskItem,
    requestTask: {
      ...mockCompletedNonComplianceApplicationSubmitRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedNonComplianceApplicationSubmitRequestTaskItem.requestTask.payload,
        ...nonCompliancePart,
        sectionCompleted:
          sectionCompleted !== undefined
            ? sectionCompleted
            : (mockCompletedNonComplianceApplicationSubmitRequestTaskItem.requestTask.payload as any).sectionCompleted,
      } as RequestTaskPayload,
    },
  };
}

export const mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: [
    'NON_COMPLIANCE_SUBMIT_APPLICATION',
    'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR',
    'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW',
  ],
  requestTask: {
    id: 1,
    type: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE',
    payload: {
      payloadType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PAYLOAD',
      comments: 'some comments',
      dailyPenaltyNotice: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
      nonComplianceAttachments: { '2b587c89-1973-42ba-9682-b3ea5453b9dd': 'supportingDoc1.pdf' },
      dailyPenaltyCompleted: true,
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    accountId: 22,
    competentAuthority: 'ENGLAND',
    id: 'DRE00022-2022-1',
    type: 'NON_COMPLIANCE',
  },
};

export function updateMockedDailyPenaltyNotice(
  nonCompliancePart?: Partial<NonComplianceDailyPenaltyNoticeRequestTaskPayload>,
  dailyPenaltyCompleted?: boolean,
): RequestTaskItemDTO {
  return {
    ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem,
    requestTask: {
      ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask.payload,
        ...nonCompliancePart,
        dailyPenaltyCompleted:
          dailyPenaltyCompleted !== undefined
            ? dailyPenaltyCompleted
            : (mockCompletedNonComplianceDailyPenaltyNoticeRequestTaskItem.requestTask.payload as any)
                .dailyPenaltyCompleted,
      } as RequestTaskPayload,
    },
  };
}

export const mockCompletedNonComplianceNoticeOfIntentRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: [
    'NON_COMPLIANCE_SUBMIT_APPLICATION',
    'NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR',
    'NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW',
  ],
  requestTask: {
    id: 1,
    type: 'NON_COMPLIANCE_NOTICE_OF_INTENT',
    payload: {
      payloadType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PAYLOAD',
      comments: 'some comments',
      noticeOfIntent: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
      nonComplianceAttachments: { '2b587c89-1973-42ba-9682-b3ea5453b9dd': 'supportingDoc1.pdf' },
      noticeOfIntentCompleted: true,
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    accountId: 22,
    competentAuthority: 'ENGLAND',
    id: 'DRE00022-2022-1',
    type: 'NON_COMPLIANCE',
  },
};

export function updateMockedNoticeOfIntent(
  nonCompliancePart?: Partial<NonComplianceNoticeOfIntentRequestTaskPayload>,
  noticeOfIntentCompleted?: boolean,
): RequestTaskItemDTO {
  return {
    ...mockCompletedNonComplianceNoticeOfIntentRequestTaskItem,
    requestTask: {
      ...mockCompletedNonComplianceNoticeOfIntentRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedNonComplianceNoticeOfIntentRequestTaskItem.requestTask.payload,
        ...nonCompliancePart,
        noticeOfIntentCompleted:
          noticeOfIntentCompleted !== undefined
            ? noticeOfIntentCompleted
            : (mockCompletedNonComplianceNoticeOfIntentRequestTaskItem.requestTask.payload as any)
                .noticeOfIntentCompleted,
      } as RequestTaskPayload,
    },
  };
}

export const mockCompletedNonComplianceCivilPenaltyRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: [
    'NON_COMPLIANCE_SUBMIT_APPLICATION',
    'NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR',
    'NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW',
  ],
  requestTask: {
    id: 1,
    type: 'NON_COMPLIANCE_CIVIL_PENALTY',
    payload: {
      payloadType: 'NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD',
      comments: 'some comments',
      civilPenalty: '2b587c89-1973-42ba-9682-b3ea5453b9dd',
      nonComplianceAttachments: { '2b587c89-1973-42ba-9682-b3ea5453b9dd': 'supportingDoc1.pdf' },
      civilPenaltyCompleted: true,
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    accountId: 22,
    competentAuthority: 'ENGLAND',
    id: 'DRE00022-2022-1',
    type: 'NON_COMPLIANCE',
  },
};

export function updateMockedCivilPenalty(
  nonCompliancePart?: Partial<NonComplianceCivilPenaltyRequestTaskPayload>,
  civilPenaltyCompleted?: boolean,
): RequestTaskItemDTO {
  return {
    ...mockCompletedNonComplianceCivilPenaltyRequestTaskItem,
    requestTask: {
      ...mockCompletedNonComplianceCivilPenaltyRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedNonComplianceCivilPenaltyRequestTaskItem.requestTask.payload,
        ...nonCompliancePart,
        civilPenaltyCompleted:
          civilPenaltyCompleted !== undefined
            ? civilPenaltyCompleted
            : (mockCompletedNonComplianceCivilPenaltyRequestTaskItem.requestTask.payload as any).civilPenaltyCompleted,
      } as RequestTaskPayload,
    },
  };
}

export const mockCompletedNonComplianceConclusionRequestTaskItem: RequestTaskItemDTO = {
  allowedRequestTaskActions: ['NON_COMPLIANCE_FINAL_DETERMINATION_SUBMIT_APPLICATION'],
  requestTask: {
    id: 1,
    type: 'NON_COMPLIANCE_FINAL_DETERMINATION',
    payload: {
      payloadType: 'NON_COMPLIANCE_FINAL_DETERMINATION_PAYLOAD',
      complianceRestored: false,
      comments: 'some comments',
      reissuePenalty: false,
      determinationCompleted: true,
    } as RequestTaskPayload,
    assignable: true,
  },
  requestInfo: {
    accountId: 22,
    competentAuthority: 'ENGLAND',
    id: 'DRE00022-2022-1',
    type: 'NON_COMPLIANCE',
  },
};

export function updateMockedConclusion(
  nonCompliancePart?: Partial<NonComplianceFinalDeterminationRequestTaskPayload>,
  determinationCompleted?: boolean,
): RequestTaskItemDTO {
  return {
    ...mockCompletedNonComplianceConclusionRequestTaskItem,
    requestTask: {
      ...mockCompletedNonComplianceConclusionRequestTaskItem.requestTask,
      payload: {
        ...mockCompletedNonComplianceConclusionRequestTaskItem.requestTask.payload,
        ...nonCompliancePart,
        determinationCompleted:
          determinationCompleted !== undefined
            ? determinationCompleted
            : (mockCompletedNonComplianceConclusionRequestTaskItem.requestTask.payload as any).determinationCompleted,
      } as RequestTaskPayload,
    },
  };
}
