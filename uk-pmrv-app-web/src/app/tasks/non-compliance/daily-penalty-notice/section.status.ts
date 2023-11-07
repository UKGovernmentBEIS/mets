import { NonComplianceDailyPenaltyNoticeRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function resolveSectionStatus(payload: NonComplianceDailyPenaltyNoticeRequestTaskPayload): TaskItemStatus {
  return payload.dailyPenaltyCompleted ? 'complete' : !payload.dailyPenaltyNotice ? 'not started' : 'in progress';
}
