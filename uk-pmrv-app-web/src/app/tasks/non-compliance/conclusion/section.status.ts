import { NonComplianceFinalDeterminationRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function resolveSectionStatus(payload: NonComplianceFinalDeterminationRequestTaskPayload): TaskItemStatus {
  return payload.determinationCompleted
    ? 'complete'
    : payload.complianceRestored === undefined
    ? 'not started'
    : 'in progress';
}
