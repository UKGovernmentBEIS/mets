import { WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function isWizardComplete(
  payload: WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload,
): boolean {
  const withholdingWithdrawal = payload?.withholdingWithdrawal;

  return !!withholdingWithdrawal && !!withholdingWithdrawal.reason;
}

export function getSectionStatus(
  payload: WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload,
): TaskItemStatus {
  return payload?.sectionsCompleted['WITHDRAWAL_REASON_CHANGE']
    ? 'complete'
    : payload?.sectionsCompleted['WITHDRAWAL_REASON_CHANGE'] === false
    ? 'in progress'
    : 'not started';
}
