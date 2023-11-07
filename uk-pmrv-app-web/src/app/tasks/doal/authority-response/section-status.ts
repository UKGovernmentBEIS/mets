import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { DoalAuthorityResponseRequestTaskPayload, RequestTaskItemDTO } from 'pmrv-api';

import { DoalAuthorityTaskSectionKey } from '../core/doal-task.type';
import { resolveStatusBySectionsCompleted } from '../core/section-status';

export function resolveSectionStatus(
  payload: DoalAuthorityResponseRequestTaskPayload,
  section: DoalAuthorityTaskSectionKey,
): TaskItemStatus {
  return resolveStatusBySectionsCompleted(payload?.doalSectionsCompleted, section);
}

export function canNotifyOperator(
  payload: DoalAuthorityResponseRequestTaskPayload,
  allowedActions: RequestTaskItemDTO['allowedRequestTaskActions'],
): boolean {
  return (
    areAllSectionsCompleted(payload) && allowedActions.includes('DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION')
  );
}

function areAllSectionsCompleted(payload: DoalAuthorityResponseRequestTaskPayload): boolean {
  return (
    resolveSectionStatus(payload, 'dateSubmittedToAuthority') === 'complete' &&
    resolveSectionStatus(payload, 'authorityResponse') === 'complete'
  );
}
