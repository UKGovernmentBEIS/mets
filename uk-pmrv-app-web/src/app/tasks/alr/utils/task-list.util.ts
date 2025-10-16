import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  ALRApplicationAuthorityReviewOutcome,
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRApplicationSubmitRequestTaskPayload,
  ALRApplicationVerificationSubmitRequestTaskPayload,
  ALRAuthorityResponseSubmitRequestTaskPayload,
  ALRClosedDetermination,
  DoalProceedToAuthorityDetermination,
  RequestTaskDTO,
} from 'pmrv-api';

import { AlrService } from '../core';

export const resolveSectionStatus = (
  payload: ALRApplicationSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (statusKey === 'sendReport') {
    return submitWizardComplete(payload) ? 'not started' : 'cannot start yet';
  }

  if (payload?.alrSectionsCompleted?.[statusKey] !== undefined) {
    return payload?.alrSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
  }

  return 'not started';
};

export const resolveVerifierSectionStatus = (
  payload: ALRApplicationVerificationSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (statusKey === 'sendReport') {
    return submitVerificationWizardComplete(payload) ? 'not started' : 'cannot start yet';
  }

  if (statusKey === 'activity') {
    return 'complete';
  }

  if (payload?.verificationSectionsCompleted?.[statusKey]?.[0] !== undefined) {
    return payload?.verificationSectionsCompleted?.[statusKey]?.[0] === true ? 'complete' : 'in progress';
  }
  return 'not started';
};

export function submitVerificationWizardComplete(payload: ALRApplicationVerificationSubmitRequestTaskPayload): boolean {
  return (
    payload?.verificationSectionsCompleted?.['opinionStatement']?.[0] === true &&
    payload?.verificationSectionsCompleted?.['overallDecision']?.[0] === true
  );
}

export const taskListTitle = (requestTaskType: RequestTaskDTO['type'], year: number, isFinal: boolean) => {
  const itemNamePipe = new ItemNamePipe();

  return itemNamePipe.transform(requestTaskType, year, isFinal);
};

export function activityComplete(payload: ALRApplicationSubmitRequestTaskPayload): boolean {
  if (payload?.alr?.alrFile) {
    return true;
  }
  return false;
}

export function submitWizardComplete(payload: ALRApplicationSubmitRequestTaskPayload): boolean {
  if (payload?.payloadType === 'ALR_APPLICATION_AMENDS_SUBMIT_PAYLOAD') {
    return (
      payload?.alrSectionsCompleted?.['activity'] === true &&
      payload?.alrSectionsCompleted?.['changesRequested'] === true
    );
  }

  return payload?.alrSectionsCompleted?.['activity'] === true;
}

export const waitTasksAlr: Array<RequestTaskDTO['type']> = [
  'ALR_WAIT_FOR_VERIFICATION',
  'ALR_AMEND_WAIT_FOR_VERIFICATION',
  'ALR_WAIT_FOR_REGULATOR_REVIEW',
  'ALR_WAIT_FOR_AMENDS',
  'ALR_WAIT_FOR_AUTHORITY_REVIEW',
  'ALR_WAIT_FOR_PEER_REVIEW',
];

export const warningTextAlr: Partial<Record<RequestTaskDTO['type'], string>> = {
  ALR_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement.',
  ALR_AMEND_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement.',
  ALR_WAIT_FOR_REGULATOR_REVIEW: 'Waiting for the regulator to complete the review',
  ALR_WAIT_FOR_AMENDS: 'You cannot edit the report as the operator is due to amend it',
  ALR_WAIT_FOR_AUTHORITY_REVIEW: 'Waiting for the authority to complete the review',
  ALR_WAIT_FOR_PEER_REVIEW: 'Waiting for peer review, you cannot make any changes',
};

export const alrSendReportTitleResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as ALRApplicationSubmitRequestTaskPayload;
  return payload?.verificationPerformed ? 'Send to regulator' : 'Send report for verification';
};

export const resolveRegulatorSectionStatus = (
  payload: ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (statusKey === 'DETERMINATION') {
    return submitRegulatorWizardComplete(payload)
      ? payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined
        ? payload?.regulatorReviewSectionsCompleted?.[statusKey] === true
          ? 'complete'
          : 'in progress'
        : 'not started'
      : 'cannot start yet';
  }

  if (statusKey === 'ALC') {
    if (payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined) {
      return payload?.regulatorReviewSectionsCompleted?.[statusKey] === true ? 'complete' : 'in progress';
    } else {
      return 'not started';
    }
  }

  if (statusKey === 'ALR' && payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined) {
    return payload.regulatorReviewGroupDecisions?.[statusKey]?.['type'] === 'ACCEPTED'
      ? 'accepted'
      : 'operator to amend';
  }
  if (
    (statusKey === 'OPINION_STATEMENT' || statusKey === 'OVERALL_DECISION') &&
    payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined
  ) {
    return payload.regulatorReviewGroupDecisions?.[statusKey]?.['type'] === 'ACCEPTED' ? 'accepted' : 'undecided';
  }

  return 'undecided';
};

export function submitReviewActivityAndOpinionStatementComplete(
  payload: ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
): boolean {
  return (
    payload?.regulatorReviewSectionsCompleted?.['ALR'] === true &&
    payload?.regulatorReviewSectionsCompleted?.['OPINION_STATEMENT'] === true &&
    payload?.regulatorReviewSectionsCompleted?.['OVERALL_DECISION'] === true &&
    payload?.regulatorReviewGroupDecisions?.['ALR']?.['type'] === 'ACCEPTED'
  );
}

export const submitRegulatorWizardComplete = (
  payload: ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
): boolean => {
  return (
    submitReviewActivityAndOpinionStatementComplete(payload) &&
    payload?.regulatorReviewSectionsCompleted?.['ALC'] === true
  );
};

export const allSectionsReviewComplete = (payload: ALRApplicationRegulatorReviewSubmitRequestTaskPayload): boolean => {
  return (
    submitRegulatorWizardComplete(payload) && payload?.regulatorReviewSectionsCompleted?.['DETERMINATION'] === true
  );
};

export function isDeterminationPopulated(determination: DoalProceedToAuthorityDetermination | ALRClosedDetermination) {
  return (
    determination !== undefined &&
    !!determination.type &&
    ((determination.type === 'CLOSED_ALR' && determination.reason) ||
      (determination.type === 'PROCEED_TO_AUTHORITY' &&
        (determination as DoalProceedToAuthorityDetermination).articleReasonItems &&
        (determination as DoalProceedToAuthorityDetermination).reason &&
        (determination as DoalProceedToAuthorityDetermination).hasWithholdingOfAllowances !== null &&
        (determination as DoalProceedToAuthorityDetermination).hasWithholdingOfAllowances !== undefined &&
        (determination as DoalProceedToAuthorityDetermination).needsOfficialNotice !== null &&
        (determination as DoalProceedToAuthorityDetermination).needsOfficialNotice !== undefined))
  );
}

export const determinationBacklinkResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;

  const determination = payload.regulatorReviewOutcome.determination as
    | DoalProceedToAuthorityDetermination
    | ALRClosedDetermination;

  const sectionCompletedExist =
    payload?.regulatorReviewSectionsCompleted?.['DETERMINATION'] !== undefined &&
    payload?.regulatorReviewSectionsCompleted?.['DETERMINATION'] !== null;

  const wizardCompleted = isDeterminationPopulated(determination) && sectionCompletedExist;

  return wizardCompleted ? '../summary' : null;
};

export const alrSendReportBacklinkResolver: ResolveFn<string> = (route) => {
  const sendTo = route.queryParams?.sendTo;

  return sendTo ? './question' : null;
};

export const resolveAuthoritySectionStatus = (
  payload: ALRAuthorityResponseSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (payload?.authorityReviewSectionsCompleted?.[statusKey] !== undefined) {
    return payload?.authorityReviewSectionsCompleted?.[statusKey] === true ? 'complete' : 'in progress';
  } else {
    return statusKey === 'upload' ? 'complete' : 'not started';
  }
};

export const isAuthorityDateSubmittedWizardCompleted = (
  authorityReviewOutcome: ALRApplicationAuthorityReviewOutcome,
) => {
  const { submissionDate } = authorityReviewOutcome || ({} as ALRApplicationAuthorityReviewOutcome);

  return !!submissionDate;
};

export const allSectionsAuthorityComplete = (payload: ALRAuthorityResponseSubmitRequestTaskPayload): boolean => {
  return (
    payload?.authorityReviewSectionsCompleted?.['applicationSubmitted'] &&
    payload?.authorityReviewSectionsCompleted?.['authorityResponse'] &&
    authorityActivityComplete(payload)
  );
};

export const authorityActivityComplete = (payload: ALRAuthorityResponseSubmitRequestTaskPayload): boolean => {
  return resolveAuthoritySectionStatus(payload, 'upload') === 'complete';
};

export const alrUploadActivityBacklinkResolver: ResolveFn<string> = () => {
  const alrService = inject(AlrService);
  const payload = alrService.payload() as ALRAuthorityResponseSubmitRequestTaskPayload;

  return authorityActivityComplete(payload) ? '../summary' : '../latest-activity';
};
