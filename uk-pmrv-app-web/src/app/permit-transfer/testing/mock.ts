import { mockPermitCompletePayload } from '@permit-application/testing/mock-permit-apply-action';
import { mockState } from '@permit-application/testing/mock-state';

import { PermitTransferDetailsConfirmation } from 'pmrv-api';

import { PermitTransferState } from '../store/permit-transfer.state';

export const mockPermitTransferSubmitPayload: PermitTransferState = {
  ...mockState,
  ...mockPermitCompletePayload,
  permitTransferDetails: {
    payer: 'RECEIVER',
    reason: 'Transfer reason',
    aerLiable: 'RECEIVER',
    transferCode: '826413660',
    transferDate: '2023-02-03',
    reasonAttachments: [],
  },
  payloadType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT_PAYLOAD',
  requestTaskType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
  requestType: 'PERMIT_TRANSFER_B',
};

export const mockPermitTransferDetailsConfirmation = {
  detailsAccepted: true,
  regulatedActivitiesInOperation: true,
  transferAccepted: false,
  transferRejectedReason: 'Transfer rejected reason',
} as PermitTransferDetailsConfirmation;

export const mockPermitTransferReviewPayload: PermitTransferState = {
  ...mockState,
  ...mockPermitCompletePayload,
  permitType: 'GHGE',
  reviewAttachments: {},
  reviewGroupDecisions: {},
  reviewSectionsCompleted: {},
  requestTaskType: 'PERMIT_TRANSFER_B_APPLICATION_REVIEW',
  payloadType: 'PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD',
  assignee: { assigneeFullName: 'John Doe', assigneeUserId: '100' },
  userAssignCapable: true,
  competentAuthority: 'ENGLAND',
  accountId: 1,
  requestTaskId: 237,
  isRequestTask: true,
  isEditable: true,
  assignable: true,
  permitTransferDetails: undefined,
  permitTransferDetailsConfirmation: undefined,
  permitTransferDetailsConfirmationDecision: undefined,
  determination: undefined,
  requestType: 'PERMIT_TRANSFER_B',
};

export const mockRequestTaskAction = (value?: any) => {
  return {
    requestTaskActionType: 'PERMIT_TRANSFER_B_SAVE_APPLICATION',
    requestTaskId: mockPermitTransferSubmitPayload.requestTaskId,
    requestTaskActionPayload: {
      permitType: 'GHGE',
      payloadType: 'PERMIT_TRANSFER_B_SAVE_APPLICATION_PAYLOAD',
      permit: mockPermitTransferSubmitPayload.permit,
      permitSectionsCompleted: {
        ...mockPermitTransferSubmitPayload.permitSectionsCompleted,
        transferDetails: [true],
      },
      permitTransferDetailsConfirmation:
        value ||
        ({
          detailsAccepted: false,
          detailsRejectedReason: 'Details rejected reason',
          regulatedActivitiesInOperation: false,
          regulatedActivitiesNotInOperationReason: 'Regulated activities reason',
          transferAccepted: false,
          transferRejectedReason: 'Transfer rejected reason',
        } as PermitTransferDetailsConfirmation),
    },
  };
};
