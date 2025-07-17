import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  ALRApplicationSubmitRequestTaskPayload,
  ALRApplicationVerificationSubmitRequestTaskPayload,
  RequestTaskDTO,
} from 'pmrv-api';

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

export const taskListTitle = (requestTaskType: RequestTaskDTO['type'], year: number) => {
  const itemNamePipe = new ItemNamePipe();

  switch (requestTaskType) {
    case 'ALR_APPLICATION_SUBMIT':
      return `Complete ${year} activity level report`;

    case 'ALR_WAIT_FOR_VERIFICATION':
      return 'Activity level report send to verifier';

    case 'ALR_APPLICATION_VERIFICATION_SUBMIT':
      return `Verify ${year} activity level report`;

    default:
      itemNamePipe.transform(requestTaskType, year);
  }
};

export function activityComplete(payload: ALRApplicationSubmitRequestTaskPayload): boolean {
  if (payload?.alr?.alrFile) {
    return true;
  }
  return false;
}

export function submitWizardComplete(payload: ALRApplicationSubmitRequestTaskPayload): boolean {
  return payload?.alrSectionsCompleted?.['activity'] === true;
}

export const waitTasksAlr: Array<RequestTaskDTO['type']> = ['ALR_WAIT_FOR_VERIFICATION'];

export const warningTextAlr: Partial<Record<RequestTaskDTO['type'], string>> = {
  ALR_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement.',
};
