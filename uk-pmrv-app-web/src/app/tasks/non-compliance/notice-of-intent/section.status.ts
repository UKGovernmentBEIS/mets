import { NonComplianceNoticeOfIntentRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export function resolveSectionStatus(payload: NonComplianceNoticeOfIntentRequestTaskPayload): TaskItemStatus {
  return payload.noticeOfIntentCompleted ? 'complete' : !payload.noticeOfIntent ? 'not started' : 'in progress';
}
