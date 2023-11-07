import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function resolveSectionStatus(payload: NonComplianceCivilPenaltyRequestTaskPayload): TaskItemStatus {
  return payload.civilPenaltyCompleted ? 'complete' : !payload.civilPenalty ? 'not started' : 'in progress';
}
