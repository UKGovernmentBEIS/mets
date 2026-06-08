import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { RequestActionDTO } from 'pmrv-api';

import { PaymentState } from '../store/payment.state';

export function shouldHidePaymentAmount(state: PaymentState): boolean {
  return (
    (state.competentAuthority === 'SCOTLAND' &&
      ['PERMIT_VARIATION', 'NER', 'PERMIT_TRANSFER_A', 'PERMIT_TRANSFER_B'].includes(state.requestType)) ||
    (state.requestType === 'PERMIT_VARIATION' && state.competentAuthority === 'WALES')
  );
}

export function getPaymentBaseLink(requestType: RequestActionDTO['requestType']): string {
  return AVIATION_REQUEST_TYPES.includes(requestType) ? 'aviation/' : '';
}
