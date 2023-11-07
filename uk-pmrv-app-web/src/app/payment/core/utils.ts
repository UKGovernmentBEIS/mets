import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { RequestActionDTO } from 'pmrv-api';

import { PaymentState } from '../store/payment.state';

export function shouldHidePaymentAmount(state: PaymentState): boolean {
  return state.requestType === 'PERMIT_VARIATION' && ['SCOTLAND', 'WALES'].includes(state.competentAuthority);
}

export function getPaymentBaseLink(requestType: RequestActionDTO['requestType']): string {
  return AVIATION_REQUEST_TYPES.includes(requestType) ? 'aviation/' : '';
}
