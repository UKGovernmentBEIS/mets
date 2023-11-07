import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { isFeeDueDateValid } from '../core/section-status';

export function resolveSectionStatus(payload: DreApplicationSubmitRequestTaskPayload): TaskItemStatus {
  return !isFeeDueDateValid(payload)
    ? 'needs review'
    : payload.sectionCompleted
    ? 'complete'
    : !payload.dre || !Object.keys(payload.dre).length
    ? 'not started'
    : 'in progress';
}
