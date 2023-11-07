import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function resolveSectionStatus(payload: NonComplianceApplicationSubmitRequestTaskPayload): TaskItemStatus {
  return payload.sectionCompleted ? 'complete' : !payload.reason ? 'not started' : 'in progress';
}
