import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
  HSETIApplicationSubmitRequestTaskPayload,
  HSETIRegulatorReviewOverallDecision,
  RequestTaskDTO,
} from 'pmrv-api';

export const resolveSectionStatus = (
  payload: HSETIApplicationSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (statusKey === 'sendReport') {
    if (payload?.payloadType === 'HSE_TI_APPLICATION_AMENDS_SUBMIT_PAYLOAD') {
      return submitWizardComplete(payload) && amendsComplete(payload) ? 'not started' : 'cannot start yet';
    }
    return submitWizardComplete(payload) ? 'not started' : 'cannot start yet';
  }

  if (payload?.hsetiSectionsCompleted?.[statusKey] !== undefined) {
    return payload?.hsetiSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
  }

  return 'not started';
};

export function detailsComplete(payload: HSETIApplicationSubmitRequestTaskPayload): boolean {
  if (payload?.hseti?.hsetiFile) {
    return true;
  }
  return false;
}

export const amendsComplete = (payload: HSETIApplicationSubmitRequestTaskPayload): boolean => {
  return payload?.hsetiSectionsCompleted?.['changesRequested'] === true;
};

export function submitWizardComplete(payload: HSETIApplicationSubmitRequestTaskPayload): boolean {
  return payload?.hsetiSectionsCompleted?.['details'] === true;
}

export const taskListTitle = (requestTaskType: RequestTaskDTO['type'], allocationPeriod: string) => {
  const itemNamePipe = new ItemNamePipe();
  const allocationPeriodYears = allocationPeriod.split('_')[1] + '-' + allocationPeriod.split('_')[2];

  switch (requestTaskType) {
    case 'HSE_TI_APPLICATION_REGULATOR_REVIEW_SUBMIT':
      return ` Review ${allocationPeriodYears} HSE target increase application`;

    default:
      return itemNamePipe.transform(requestTaskType, allocationPeriodYears);
  }
};

export const resolveRegulatorSectionStatus = (
  payload: HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (statusKey === 'OVERALL_DECISION') {
    const decisionTypes: Record<HSETIRegulatorReviewOverallDecision['type'], TaskItemStatus> = {
      APPROVED: 'approved',
      REJECTED: 'rejected',
      DEEMED_WITHDRAWN: 'deemed withdrawn',
      WITHDRAWN: 'withdrawn',
    };
    return submitRegulatorWizardComplete(payload)
      ? payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined
        ? payload?.regulatorReviewSectionsCompleted?.[statusKey] === true
          ? decisionTypes[payload?.overallDecision?.type]
          : 'in progress'
        : 'undecided'
      : 'cannot start yet';
  }

  if (statusKey === 'HSETI') {
    if (payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined) {
      return payload?.regulatorReviewGroupDecisions?.[statusKey]?.['type'] === 'ACCEPTED'
        ? 'accepted'
        : payload?.regulatorReviewGroupDecisions?.[statusKey]?.['type'] === 'REJECTED'
          ? 'rejected'
          : 'operator to amend';
    } else {
      return 'undecided';
    }
  }

  return 'undecided';
};

export function submitRegulatorWizardComplete(
  payload: HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
): boolean {
  return (
    payload?.regulatorReviewSectionsCompleted?.['HSETI'] === true &&
    payload?.regulatorReviewGroupDecisions?.['HSETI']?.['type'] !== 'OPERATOR_AMENDS_NEEDED'
  );
}

export const submitRegulatorAllSectionsComplete = (
  payload: HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
): boolean => {
  payload.hsetiSectionsCompleted['details'] = true;
  return (
    submitRegulatorWizardComplete(payload) && payload?.regulatorReviewSectionsCompleted?.['OVERALL_DECISION'] === true
  );
};

export function isOverallDecisionPopulated(overallDecision: HSETIRegulatorReviewOverallDecision): boolean {
  return overallDecision !== undefined && !!overallDecision.type
    ? (overallDecision as HSETIRegulatorReviewOverallDecision).type !== null &&
        (overallDecision as HSETIRegulatorReviewOverallDecision).type !== undefined
    : true;
}
