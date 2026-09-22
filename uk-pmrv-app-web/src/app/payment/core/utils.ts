import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { RequestActionDTO } from 'pmrv-api';

export function getPaymentBaseLink(requestType: RequestActionDTO['requestType']): string {
  return AVIATION_REQUEST_TYPES.includes(requestType) ? 'aviation/' : '';
}
