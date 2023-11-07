import { initialState } from '@permit-application/store/permit-application.state';

import { RequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

import { PermitIssuanceState } from '../../permit-issuance/store/permit-issuance.state';
import { mockPermitApplyPayload } from './mock-permit-apply-action';

export const mockState: PermitIssuanceState = {
  ...initialState,
  ...mockPermitApplyPayload,
  reviewSectionsCompleted: {},
  permitType: 'GHGE',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  paymentCompleted: false,
  requestTaskId: 237,
  requestId: '1',
  isRequestTask: true,
  isEditable: true,
  assignable: true,
  userViewRole: 'OPERATOR',
  requestType: 'PERMIT_ISSUANCE',
};

export const mockRequestActionState: PermitIssuanceState = {
  ...initialState,
  ...mockPermitApplyPayload,
  reviewSectionsCompleted: {},
  permitType: 'GHGE',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: undefined,
  actionId: 1,
  isRequestTask: false,
  isEditable: false,
  assignable: false,
  userViewRole: 'OPERATOR',
  requestType: 'PERMIT_ISSUANCE',
};

export const mockReviewState: PermitIssuanceState = {
  ...initialState,
  ...mockPermitApplyPayload,
  permitType: 'GHGE',
  reviewAttachments: {},
  reviewGroupDecisions: {},
  reviewSectionsCompleted: {},
  payloadType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: 237,
  isRequestTask: true,
  isEditable: true,
  assignable: true,
  requestType: 'PERMIT_ISSUANCE',
};

export const mockReviewRequestActionState: PermitIssuanceState = {
  ...initialState,
  ...mockPermitApplyPayload,
  permitType: 'GHGE',
  reviewAttachments: {},
  reviewGroupDecisions: {},
  reviewSectionsCompleted: {},
  payloadType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: undefined,
  actionId: 1,
  isRequestTask: false,
  isEditable: false,
  assignable: false,
  requestType: 'PERMIT_ISSUANCE',
};

export function mockStateBuild(value?: any, status?: any): PermitIssuanceState {
  return {
    ...mockState,
    permitSectionsCompleted: {
      ...mockState.permitSectionsCompleted,
      ...status,
    },
    permit: {
      ...mockState.permit,
      ...value,
    },
  };
}

export function mockReviewStateBuild(value?: any, status?: any): PermitIssuanceState {
  return {
    ...mockReviewState,
    determination: {
      ...mockReviewState.determination,
      ...value,
    },
    reviewSectionsCompleted: {
      ...mockReviewState.reviewSectionsCompleted,
      ...status,
    },
  };
}

export function mockDeterminationPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION',
    requestTaskId: mockReviewState.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD',
      determination: {
        ...mockState.determination,
        ...value,
      },
      reviewSectionsCompleted: {
        ...mockState.reviewSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_APPLICATION',
    requestTaskId: mockState.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      permitType: mockState.permitType,
      permit: {
        ...mockState.permit,
        ...value,
      },
      permitSectionsCompleted: {
        ...mockState.permitSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}

export function mockDecisionPostBuild(value?: any, status?: any): RequestTaskActionProcessDTO {
  return {
    requestTaskActionType: 'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION',
    requestTaskId: mockReviewState.requestTaskId,
    requestTaskActionPayload: {
      payloadType: 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
      ...mockReviewState,
      reviewGroupDecisions: {
        ...mockReviewState.reviewGroupDecisions,
        ...value,
      },
      reviewSectionsCompleted: {
        ...mockReviewState.reviewSectionsCompleted,
        ...status,
      },
    } as RequestTaskActionPayload,
  };
}
