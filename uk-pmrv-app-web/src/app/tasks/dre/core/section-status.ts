import moment from 'moment';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function isFeeDueDateValid(payload: DreApplicationSubmitRequestTaskPayload): boolean {
  const feeDate = moment(payload?.dre?.fee?.feeDetails?.dueDate).format('YYYY-MM-DD');

  const currentDate = moment().format('YYYY-MM-DD');

  return feeDate >= currentDate;
}
