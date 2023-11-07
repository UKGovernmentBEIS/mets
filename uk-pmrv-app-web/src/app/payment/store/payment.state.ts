import {
  PaymentCancelledRequestActionPayload,
  PaymentConfirmRequestTaskPayload,
  PaymentMakeRequestTaskPayload,
  PaymentProcessedRequestActionPayload,
  PaymentTrackRequestTaskPayload,
  RequestInfoDTO,
  RequestTaskItemDTO,
} from 'pmrv-api';

export interface PaymentState {
  requestId: string;
  requestType: RequestInfoDTO['type'];
  competentAuthority: RequestInfoDTO['competentAuthority'];
  requestTaskId?: number;
  requestTaskItem?: RequestTaskItemDTO;
  isEditable: boolean;
  paymentDetails?: PaymentMakeRequestTaskPayload | PaymentTrackRequestTaskPayload | PaymentConfirmRequestTaskPayload;
  selectedPaymentMethod?: 'BANK_TRANSFER' | 'CREDIT_OR_DEBIT_CARD';
  markedAsPaid?: boolean;
  completed?: boolean;

  // Request action info
  requestActionId?: number;
  requestActionCreationDate?: string;
  actionPayload?: PaymentProcessedRequestActionPayload | PaymentCancelledRequestActionPayload;
}

export const initialState: PaymentState = {
  requestId: undefined,
  requestType: undefined,
  competentAuthority: undefined,
  isEditable: true,
};
