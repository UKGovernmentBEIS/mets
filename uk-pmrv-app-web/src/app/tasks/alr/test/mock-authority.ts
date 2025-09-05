import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  ALRAuthorityResponseSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

export const alrMockAuthorityPayload: ALRAuthorityResponseSubmitRequestTaskPayload = {
  payloadType: 'ALR_AUTHORITY_RESPONSE_SUBMIT_PAYLOAD',
  authorityReviewOutcome: {
    submissionDate: undefined,
    authorityResponse: { type: undefined, authorityRespondDate: undefined },
    alr: { alrFile: undefined, files: undefined },
  },
  authorityReviewSectionsCompleted: {},
  alrAttachments: {},
};

export const alrMockAuthorityState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['ALR_SAVE_AUTHORITY_RESPONSE'],
    requestInfo: {
      id: 'ALR00189-2021',
      type: 'ALR',
      competentAuthority: 'ENGLAND',
      accountId: 1,
      requestMetadata: {
        type: 'ALR',
        year: '2021',
      },
    },
    requestTask: {
      id: 1,
      type: 'ALR_AUTHORITY_RESPONSE_SUBMIT',
      payload: alrMockAuthorityPayload,
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

export function mockAlrAuthorityStateBuild(
  value?: ALRAuthorityResponseSubmitRequestTaskPayload,
  requestTaskType: RequestTaskDTO['type'] = 'ALR_AUTHORITY_RESPONSE_SUBMIT',
): CommonTasksState {
  return {
    ...alrMockAuthorityState,
    requestTaskItem: {
      ...alrMockAuthorityState.requestTaskItem,
      requestTask: {
        ...alrMockAuthorityState.requestTaskItem.requestTask,
        requestTaskType,
        payload: {
          ...alrMockAuthorityState.requestTaskItem.requestTask.payload,
          ...value,
        } as ALRAuthorityResponseSubmitRequestTaskPayload,
      },
    },
  } as CommonTasksState;
}

export function mockAlrAuthorityPostBuild(
  value?: any,
  authorityReviewSectionsCompleted?: ALRAuthorityResponseSubmitRequestTaskPayload['authorityReviewSectionsCompleted'],
): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'ALR_SAVE_AUTHORITY_RESPONSE',
    requestTaskId: 1,
    requestTaskActionPayload: {
      payloadType: 'ALR_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
      ...value,
      authorityReviewSectionsCompleted: {
        ...alrMockAuthorityPayload?.authorityReviewSectionsCompleted,
        ...authorityReviewSectionsCompleted,
      },
    } as RequestTaskActionPayload,
  };
}

export const mockAlrAuthorityCompletedPayload = {
  payloadType: 'ALR_AUTHORITY_RESPONSE_SUBMIT_PAYLOAD',
  regulatorPreliminaryAllocations: [
    {
      subInstallationName: 'PLASTER',
      year: 2030,
      allowances: 12,
      allocationId: '0',
    },
  ],
  authorityReviewOutcome: {
    submissionDate: '2024-03-13',
    authorityResponse: {
      type: 'VALID_WITH_CORRECTIONS',
      authorityRespondDate: '2024-02-11',
      preliminaryAllocations: [
        {
          year: 2024,
          allowances: 200,
          subInstallationName: 'ALUMINIUM',
          allocationId: '1',
        },
        {
          year: 2023,
          allowances: 100,
          subInstallationName: 'ALUMINIUM',
          allocationId: '2',
        },
      ],
      totalAllocationsPerYear: {
        '2023': 100,
        '2024': 200,
      },
      decisionNotice: '14545',
      documents: [],
    },
  },
  authorityReviewSectionsCompleted: {
    authorityResponse: false,
    applicationSubmitted: true,
  },
  alrAttachments: {},
};
