import BigNumber from 'bignumber.js';

import { DreFeeDetails } from 'pmrv-api';

export function calculateTotalFee(fee: DreFeeDetails): string {
  return new BigNumber(fee?.hourlyRate || 0).multipliedBy(fee?.totalBillableHours || 0).toString();
}
