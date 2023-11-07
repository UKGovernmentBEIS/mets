import {
  WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload,
  WithholdingOfAllowancesApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function isWizardComplete(payload: WithholdingOfAllowancesApplicationSubmitRequestTaskPayload): boolean {
  const withholdingOfAllowances = payload?.withholdingOfAllowances;

  return !!withholdingOfAllowances && !!withholdingOfAllowances.year && !!withholdingOfAllowances.reasonType;
}

export function getSectionStatus(
  payload:
    | WithholdingOfAllowancesApplicationSubmitRequestTaskPayload
    | WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload,
): TaskItemStatus {
  return payload?.sectionsCompleted['DETAILS_CHANGE']
    ? 'complete'
    : payload?.sectionsCompleted['DETAILS_CHANGE'] === false
    ? 'in progress'
    : 'not started';
}
