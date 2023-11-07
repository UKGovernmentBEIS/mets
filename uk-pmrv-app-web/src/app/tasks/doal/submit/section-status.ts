import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DoalTaskSectionKey } from '../core/doal-task.type';
import { resolveStatusBySectionsCompleted } from '../core/section-status';

export function resolveSectionStatus(
  payload: DoalApplicationSubmitRequestTaskPayload,
  section: DoalTaskSectionKey,
): TaskItemStatus {
  return section === 'determination'
    ? resolveDeterminationSectionStatus(payload)
    : resolveStatusBySectionsCompleted(payload?.doalSectionsCompleted ?? {}, section);
}

export function resolveDeterminationSectionStatus(payload: DoalApplicationSubmitRequestTaskPayload): TaskItemStatus {
  if (
    payload?.doalSectionsCompleted['determination'] === undefined ||
    payload?.doalSectionsCompleted['determination'] === null
  ) {
    return [
      resolveSectionStatus(payload, 'operatorActivityLevelReport'),
      resolveSectionStatus(payload, 'verificationReportOfTheActivityLevelReport'),
      resolveSectionStatus(payload, 'additionalDocuments'),
      resolveSectionStatus(payload, 'activityLevelChangeInformation'),
    ].some((sectionStatus) => sectionStatus !== 'complete')
      ? 'cannot start yet'
      : 'not started';
  } else {
    return resolveStatusBySectionsCompleted(payload.doalSectionsCompleted, 'determination');
  }
}

export function areAllSubmitSectionCompleted(payload: DoalApplicationSubmitRequestTaskPayload): boolean {
  return (
    resolveSectionStatus(payload, 'operatorActivityLevelReport') === 'complete' &&
    resolveSectionStatus(payload, 'verificationReportOfTheActivityLevelReport') === 'complete' &&
    resolveSectionStatus(payload, 'additionalDocuments') === 'complete' &&
    resolveSectionStatus(payload, 'activityLevelChangeInformation') === 'complete' &&
    resolveDeterminationSectionStatus(payload) === 'complete'
  );
}
