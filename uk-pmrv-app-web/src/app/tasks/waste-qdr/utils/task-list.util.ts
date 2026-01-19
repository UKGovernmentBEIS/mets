import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  RequestTaskDTO,
  WasteQDR,
  WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  WasteQDRApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

export const wasteQdrWaitTasks: Array<RequestTaskDTO['type']> = [
  'WASTE_QDR_WAIT_FOR_REGULATOR_REVIEW',
  'WASTE_QDR_WAIT_FOR_AMENDS',
];

export const wasteQdrWarningText: Partial<Record<RequestTaskDTO['type'], string>> = {
  WASTE_QDR_WAIT_FOR_REGULATOR_REVIEW: 'Waiting for the regulator to complete the review',
  WASTE_QDR_WAIT_FOR_AMENDS: 'You cannot edit the report as the operator is due to amend it',
};

export const submitRequestTasks: Array<RequestTaskDTO['type']> = [
  'WASTE_QDR_APPLICATION_SUBMIT',
  'WASTE_QDR_WAIT_FOR_REGULATOR_REVIEW',
  'WASTE_QDR_APPLICATION_AMENDS_SUBMIT',
];

export const amendsSubmittedTasks: Array<RequestTaskDTO['type']> = ['WASTE_QDR_APPLICATION_AMENDS_SUBMIT'];

export const wasteQdrTaskListTitle = (requestTaskType: RequestTaskDTO['type'], year: number, quarter?: string) => {
  const itemNamePipe = new ItemNamePipe();

  return itemNamePipe.transform(requestTaskType, year, quarter);
};

export const wasteQdrResolveSectionStatus = (
  payload: WasteQDRApplicationSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (statusKey === 'sendReport') {
    return wasteQdrSubmitWizardComplete(payload) ? 'not started' : 'cannot start yet';
  }

  if (payload?.wasteQDRSectionsCompleted?.[statusKey] !== undefined) {
    return payload?.wasteQDRSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
  }

  return 'not started';
};

export const resolveRegulatorSectionStatus = (
  payload: WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (payload?.regulatorReviewSectionsCompleted?.[statusKey] !== undefined) {
    return payload.reviewDecision.type === 'ACCEPTED' ? 'accepted' : 'operator to amend';
  }

  return 'undecided';
};

export function submitReviewQdrComplete(payload: WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload): boolean {
  return (
    payload?.['regulatorReviewSectionsCompleted']?.['qdr'] === true &&
    payload.reviewDecision?.type !== undefined &&
    payload.reviewDecision?.type !== null &&
    payload.reviewDecision?.type === 'ACCEPTED'
  );
}

export const submitRegulatorWizardComplete = (
  payload: WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
): boolean => {
  return submitReviewQdrComplete(payload);
};

export const wasteQdrSubmitWizardComplete = (payload: WasteQDRApplicationSubmitRequestTaskPayload): boolean => {
  const { payloadType, wasteQDRSectionsCompleted } = payload || {};
  return (
    wasteQDRSectionsCompleted?.['qdr'] === true &&
    (payloadType === 'WASTE_QDR_APPLICATION_AMENDS_SUBMIT_PAYLOAD'
      ? wasteQDRSectionsCompleted?.['changesRequested'] === true
      : true)
  );
};

export const qdrTaskCompleted = (qdr: WasteQDR) => {
  if (qdr.reportProvided === undefined) {
    return false;
  } else if (qdr.reportProvided) {
    return !!qdr.report;
  } else {
    return !!qdr.reasonForUnprovided;
  }
};
