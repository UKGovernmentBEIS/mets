import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  AviationVirApplicationReviewRequestTaskPayload,
  AviationVirApplicationSubmitRequestTaskPayload,
  VirVerificationData,
} from 'pmrv-api';

export function getSubmitStatus(
  virPayload: AviationVirApplicationSubmitRequestTaskPayload,
  statusKey: string | 'sendReport',
): TaskItemStatus {
  const completionState = virPayload.virSectionsCompleted[statusKey];
  if (statusKey === 'sendReport') {
    const flattenedReferences = getReferences(virPayload?.verificationData);
    const allReferences = flattenedReferences.every(
      (reference) => virPayload?.virSectionsCompleted?.[reference] === true,
    );

    return allReferences ? 'not started' : 'cannot start yet';
  } else {
    return completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';
  }
}

export function getReviewStatus(
  virPayload: AviationVirApplicationReviewRequestTaskPayload,
  statusKey: string | 'sendReport' | 'createSummary',
): TaskItemStatus {
  const completionState = virPayload.reviewSectionsCompleted[statusKey];
  if (statusKey === 'sendReport') {
    const flattenedReferences = getReferences(virPayload?.verificationData);
    const allReferences =
      flattenedReferences.every((reference) => virPayload?.reviewSectionsCompleted?.[reference] === true) &&
      virPayload?.reviewSectionsCompleted?.['createSummary'] === true;

    return allReferences ? 'not started' : 'cannot start yet';
  } else {
    return completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';
  }
}

export function getRespondStatus(
  virPayload: AviationVirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  statusKey: string,
  isRespondSubmit: boolean,
): TaskItemStatus {
  const completionState = virPayload.virRespondToRegulatorCommentsSectionsCompleted[statusKey];
  const referenceCompletionState =
    completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';

  return isRespondSubmit
    ? referenceCompletionState === 'complete'
      ? 'not started'
      : 'cannot start yet'
    : referenceCompletionState;
}

function getReferences(verificationData: VirVerificationData): string[] {
  return [
    ...Object.keys(verificationData?.uncorrectedNonConformities || {}),
    ...Object.keys(verificationData?.recommendedImprovements || {}),
    ...Object.keys(verificationData?.priorYearIssues || {}),
  ];
}
