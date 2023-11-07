import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ReviewGroupTasksAggregatorStatus } from '../../permit-application/review/types/review.permit.type';
import { applicableReviewGroupTasks } from '../../permit-application/review/utils/review';
import { StatusKey } from '../../permit-application/shared/types/permit-task.type';
import { resolvePermitSectionStatus } from '../../permit-application/shared/utils/permit-section-status-resolver';
import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitVariationState } from '../store/permit-variation.state';

export function resolveReviewGroupStatusRegulatorLed(
  state: PermitVariationState,
  key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
): ReviewGroupTasksAggregatorStatus {
  if (!state.isRequestTask) {
    return 'complete';
  } else {
    const tasksStatuses: TaskItemStatus[] = [];
    const tasks = applicableReviewGroupTasks(state)[key];
    tasks.forEach((task) => tasksStatuses.push(resolvePermitSectionStatus(state, task as StatusKey)));

    if (!tasksStatuses?.length) {
      return 'undecided';
    } else {
      return tasksStatuses.includes('needs review')
        ? 'needs review'
        : (['not started', 'cannot start yet'] as TaskItemStatus[]).every((status) => tasksStatuses.includes(status))
        ? 'not started'
        : tasksStatuses.every((status) => status === 'cannot start yet')
        ? 'cannot start yet'
        : tasksStatuses.every((status) => status === 'complete')
        ? 'complete'
        : (['in progress', 'cannot start yet', 'complete'] as TaskItemStatus[]).some((status) =>
            tasksStatuses.includes(status),
          )
        ? 'in progress'
        : 'not started'; //fallback, should never happen
    }
  }
}
