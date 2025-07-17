import { format } from 'date-fns';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

// eslint-disable-next-line @typescript-eslint/no-unused-vars
export function isFeeDueDateValid(payload: DreApplicationSubmitRequestTaskPayload): boolean {
  const feeDate = format(payload?.dre?.fee?.feeDetails?.dueDate ?? new Date(), 'yyyy-MM-dd');

  const currentDate = format(new Date(), 'yyyy-MM-dd');

  return feeDate >= currentDate;
}
