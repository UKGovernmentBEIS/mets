import { KeycloakProfile } from 'keycloak-js';

import { RequestInfoDTO, RequestTaskDTO } from 'pmrv-api';

import { PaymentState } from '../store/payment.state';

export interface PaymentDetails {
  amount?: number;
  paidByFullName?: string;
  paymentDate?: string;
  paymentMethod?: 'BANK_TRANSFER' | 'CREDIT_OR_DEBIT_CARD';
  paymentRefNum?: string;
  receivedDate?: string;
  status?: 'CANCELLED' | 'COMPLETED' | 'MARK_AS_PAID' | 'MARK_AS_RECEIVED';
}

export function mapMakePaymentToPaymentDetails(userProfile: KeycloakProfile, state: PaymentState): PaymentDetails {
  return {
    amount: +state.paymentDetails.amount,
    paidByFullName: userProfile.firstName + ' ' + userProfile.lastName,
    paymentDate: new Date().toISOString(),
    paymentMethod: 'BANK_TRANSFER',
    paymentRefNum: state.paymentDetails.paymentRefNum,
    status: state.markedAsPaid ? 'MARK_AS_PAID' : null,
  };
}

export function mapGOVUKToPaymentDetails(
  userProfile: KeycloakProfile,
  state: PaymentState,
  status: 'CANCELLED' | 'COMPLETED' | 'MARK_AS_PAID' | 'MARK_AS_RECEIVED',
): PaymentDetails {
  return {
    amount: +state.paymentDetails.amount,
    paidByFullName: userProfile.firstName + ' ' + userProfile.lastName,
    paymentDate: new Date().toISOString(),
    paymentMethod: 'CREDIT_OR_DEBIT_CARD',
    paymentRefNum: state.paymentDetails.paymentRefNum,
    status: status,
  };
}

export function mapTrackPaymentToPaymentDetails(state: PaymentState): PaymentDetails {
  return [
    'PERMIT_ISSUANCE_CONFIRM_PAYMENT',
    'PERMIT_SURRENDER_CONFIRM_PAYMENT',
    'PERMIT_REVOCATION_CONFIRM_PAYMENT',
    'PERMIT_VARIATION_CONFIRM_PAYMENT',
    'PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT',
    'PERMIT_TRANSFER_A_CONFIRM_PAYMENT',
    'PERMIT_TRANSFER_B_CONFIRM_PAYMENT',
    'DRE_CONFIRM_PAYMENT',
    'EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT',
    'EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT',
    'AVIATION_DRE_UKETS_CONFIRM_PAYMENT',
    'EMP_VARIATION_UKETS_CONFIRM_PAYMENT',
    'EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT',
    'EMP_VARIATION_CORSIA_REGULATOR_LED_CONFIRM_PAYMENT',
    'EMP_VARIATION_CORSIA_CONFIRM_PAYMENT',
  ].includes(state.requestTaskItem.requestTask.type)
    ? { ...state.paymentDetails, amount: +state.paymentDetails.amount }
    : {
        amount: +state.paymentDetails.amount,
        paymentRefNum: state.paymentDetails.paymentRefNum,
        status: null,
      };
}

export function getHeadingMap(year?: number): Partial<Record<RequestTaskDTO['type'], string>> {
  return {
    PERMIT_ISSUANCE_TRACK_PAYMENT: 'Payment for permit application',
    PERMIT_ISSUANCE_MAKE_PAYMENT: 'Pay permit application fee',
    PERMIT_ISSUANCE_CONFIRM_PAYMENT: 'Payment for permit application',

    PERMIT_SURRENDER_TRACK_PAYMENT: 'Payment for surrender permit application',
    PERMIT_SURRENDER_MAKE_PAYMENT: 'Pay surrender permit application fee',
    PERMIT_SURRENDER_CONFIRM_PAYMENT: 'Payment for surrender permit application',

    PERMIT_REVOCATION_TRACK_PAYMENT: 'Payment for permit revocation',
    PERMIT_REVOCATION_MAKE_PAYMENT: 'Pay permit revocation fee',
    PERMIT_REVOCATION_CONFIRM_PAYMENT: 'Payment for permit revocation',

    PERMIT_VARIATION_TRACK_PAYMENT: 'Payment for permit variation application',
    PERMIT_VARIATION_MAKE_PAYMENT: 'Pay permit variation application fee',
    PERMIT_VARIATION_CONFIRM_PAYMENT: 'Payment for permit variation application',

    PERMIT_VARIATION_REGULATOR_LED_TRACK_PAYMENT: 'Payment for permit variation application',
    PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT: 'Pay permit variation application fee',
    PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT: 'Payment for permit variation application',

    PERMIT_TRANSFER_A_TRACK_PAYMENT: 'Payment for permit transfer application',
    PERMIT_TRANSFER_A_MAKE_PAYMENT: 'Pay permit transfer application fee',
    PERMIT_TRANSFER_A_CONFIRM_PAYMENT: 'Payment for permit transfer application',

    PERMIT_TRANSFER_B_TRACK_PAYMENT: 'Payment for permit transfer application',
    PERMIT_TRANSFER_B_MAKE_PAYMENT: 'Pay permit transfer application fee',
    PERMIT_TRANSFER_B_CONFIRM_PAYMENT: 'Payment for permit transfer application',

    DRE_TRACK_PAYMENT: `Payment for ${year} reportable emissions`,
    DRE_MAKE_PAYMENT: `Pay ${year} reportable emissions fee`,
    DRE_CONFIRM_PAYMENT: `Payment for ${year} reportable emissions`,

    EMP_ISSUANCE_UKETS_TRACK_PAYMENT: 'Track payment for emissions monitoring plan application',
    EMP_ISSUANCE_UKETS_MAKE_PAYMENT: 'Pay emissions monitoring plan application fee',
    EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT: 'Track payment for emissions monitoring plan application',

    EMP_ISSUANCE_CORSIA_TRACK_PAYMENT: 'Track payment for emissions monitoring plan application',
    EMP_ISSUANCE_CORSIA_MAKE_PAYMENT: 'Pay emissions monitoring plan application fee',
    EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT: 'Track payment for emissions monitoring plan application',

    EMP_VARIATION_UKETS_TRACK_PAYMENT: 'Track payment for emissions monitoring plan variation',
    EMP_VARIATION_UKETS_MAKE_PAYMENT: 'Pay emissions monitoring plan variation fee',
    EMP_VARIATION_UKETS_CONFIRM_PAYMENT: 'Track payment for emissions monitoring plan variation',

    EMP_VARIATION_CORSIA_REGULATOR_LED_TRACK_PAYMENT: 'Track payment for emissions monitoring plan variation',
    EMP_VARIATION_CORSIA_REGULATOR_LED_MAKE_PAYMENT: 'Pay emissions monitoring plan variation fee',
    EMP_VARIATION_CORSIA_REGULATOR_LED_CONFIRM_PAYMENT: 'Track payment for emissions monitoring plan variation',

    EMP_VARIATION_CORSIA_TRACK_PAYMENT: 'Track payment for emissions monitoring plan variation',
    EMP_VARIATION_CORSIA_MAKE_PAYMENT: 'Pay emissions monitoring plan variation fee',
    EMP_VARIATION_CORSIA_CONFIRM_PAYMENT: 'Track payment for emissions monitoring plan variation',

    EMP_VARIATION_UKETS_REGULATOR_LED_TRACK_PAYMENT: 'Track payment for emissions monitoring plan variation',
    EMP_VARIATION_UKETS_REGULATOR_LED_MAKE_PAYMENT: 'Pay emissions monitoring plan variation fee',
    EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT: 'Track payment for emissions monitoring plan variation',

    AVIATION_DRE_UKETS_TRACK_PAYMENT: `Track payment for ${year} emissions determination`,
    AVIATION_DRE_UKETS_MAKE_PAYMENT: `Pay ${year} emissions determination fee`,
    AVIATION_DRE_UKETS_CONFIRM_PAYMENT: `Track payment for ${year} emissions determination`,
  };
}

export const paymentHintInfo: Partial<Record<RequestTaskDTO['type'], string>> = {
  PERMIT_ISSUANCE_TRACK_PAYMENT: 'The permit determination cannot be completed until the payment has been received',
  PERMIT_ISSUANCE_MAKE_PAYMENT: 'Your permit application cannot be processed until this payment is received',
  PERMIT_ISSUANCE_CONFIRM_PAYMENT: 'The permit determination cannot be completed until the payment has been received',

  PERMIT_SURRENDER_TRACK_PAYMENT:
    'The surrender permit determination cannot be completed until the payment has been received',
  PERMIT_SURRENDER_MAKE_PAYMENT: 'Your surrender permit application cannot be processed until this payment is received',
  PERMIT_SURRENDER_CONFIRM_PAYMENT:
    'The surrender permit determination cannot be completed until the payment has been received',

  PERMIT_REVOCATION_TRACK_PAYMENT: 'The permit revocation cannot be completed until the payment has been received',
  PERMIT_REVOCATION_MAKE_PAYMENT: 'Your permit revocation cannot be processed until this payment is received',
  PERMIT_REVOCATION_CONFIRM_PAYMENT: 'The permit revocation cannot be completed until the payment has been received',

  PERMIT_VARIATION_TRACK_PAYMENT: 'The application cannot be reviewed until payment is marked as received',
  PERMIT_VARIATION_MAKE_PAYMENT: 'Your permit variation application cannot be processed until payment is made',
  PERMIT_VARIATION_CONFIRM_PAYMENT: 'The application cannot be reviewed until payment is marked as received',

  PERMIT_VARIATION_REGULATOR_LED_TRACK_PAYMENT:
    'The application cannot be reviewed until payment is marked as received',
  PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT:
    'Your permit variation application cannot be processed until payment is made',
  PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT:
    'The application cannot be reviewed until payment is marked as received',

  PERMIT_TRANSFER_A_TRACK_PAYMENT: 'The application cannot be reviewed until payment is marked as received',
  PERMIT_TRANSFER_A_MAKE_PAYMENT: 'Your permit transfer application cannot be processed until payment is made',
  PERMIT_TRANSFER_A_CONFIRM_PAYMENT:
    'The permit transfer determination cannot be completed until the payment has been received',

  PERMIT_TRANSFER_B_TRACK_PAYMENT: 'The application cannot be reviewed until payment is marked as received',
  PERMIT_TRANSFER_B_MAKE_PAYMENT: 'Your permit transfer application cannot be processed until payment is made',
  PERMIT_TRANSFER_B_CONFIRM_PAYMENT:
    'The permit transfer determination cannot be completed until the payment has been received',

  EMP_ISSUANCE_UKETS_TRACK_PAYMENT: 'The application cannot be completed until the payment has been received',
  EMP_ISSUANCE_UKETS_MAKE_PAYMENT: 'Your application cannot be processed until this payment is received',
  EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT: 'The application cannot be completed until the payment has been received',

  EMP_VARIATION_UKETS_TRACK_PAYMENT: 'The variation cannot be completed until the payment has been received',
  EMP_VARIATION_UKETS_MAKE_PAYMENT: 'Your variation cannot be processed until this payment is received',
  EMP_VARIATION_UKETS_CONFIRM_PAYMENT: 'The variation cannot be completed until the payment has been received',

  EMP_VARIATION_UKETS_REGULATOR_LED_TRACK_PAYMENT:
    'The variation cannot be completed until the payment has been received',
  EMP_VARIATION_UKETS_REGULATOR_LED_MAKE_PAYMENT: 'Your variation cannot be processed until this payment is received',
  EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT:
    'The variation cannot be completed until the payment has been received',

  EMP_VARIATION_CORSIA_REGULATOR_LED_TRACK_PAYMENT:
    'The variation cannot be completed until the payment has been received',
  EMP_VARIATION_CORSIA_REGULATOR_LED_MAKE_PAYMENT: 'Your variation cannot be processed until this payment is received',
  EMP_VARIATION_CORSIA_REGULATOR_LED_CONFIRM_PAYMENT:
    'The variation cannot be completed until the payment has been received',

  EMP_VARIATION_CORSIA_TRACK_PAYMENT: 'The variation cannot be completed until the payment has been received',
  EMP_VARIATION_CORSIA_MAKE_PAYMENT: 'Your variation cannot be processed until this payment is received',
  EMP_VARIATION_CORSIA_CONFIRM_PAYMENT: 'The variation cannot be completed until the payment has been received',
};

export function trackShouldDisplayMarkPaidConfirmationInfo(requestType: RequestInfoDTO['type']): boolean {
  return !['DRE', 'AVIATION_DRE_UKETS'].includes(requestType);
}

export function trackShouldDisplayCancelHintInfo(requestType: RequestInfoDTO['type']): boolean {
  return !['DRE', 'AVIATION_DRE_UKETS'].includes(requestType);
}
