import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  NER,
  NerApplicationSubmitRequestTaskPayload,
  NERApplicationVerificationSubmitRequestTaskPayload,
  RequestTaskDTO,
} from 'pmrv-api';

export const nerTaskListTitle = (requestTaskType: RequestTaskDTO['type']) => {
  const itemNamePipe = new ItemNamePipe();

  return itemNamePipe.transform(requestTaskType);
};

export const nerWaitTasks: Array<RequestTaskDTO['type']> = ['NER_WAIT_FOR_VERIFICATION'];

export const nerWarningText: Partial<Record<RequestTaskDTO['type'], string>> = {
  NER_WAIT_FOR_VERIFICATION: 'Waiting for the verifier to complete the opinion statement',
};

export const nerSubmitRequestTasks: Array<RequestTaskDTO['type']> = [
  'NER_APPLICATION_SUBMIT',
  'NER_APPLICATION_VERIFICATION_SUBMIT',
];

export const nerResolveSectionStatus = (
  payload: NerApplicationSubmitRequestTaskPayload | NERApplicationVerificationSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (statusKey === 'details') {
    if (payload?.payloadType === 'NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD') {
      return 'complete';
    }
  }

  if (statusKey === 'sendReport') {
    if (payload?.payloadType === 'NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD') {
      return submitVerificationWizardComplete(payload) ? 'not started' : 'cannot start yet';
    }
    return nerSubmitWizardComplete(payload) ? 'not started' : 'cannot start yet';
  }

  if (payload?.nerSectionsCompleted?.[statusKey] !== undefined) {
    return payload?.nerSectionsCompleted[statusKey] === true ? 'complete' : 'in progress';
  }

  return 'not started';
};

export const nerSubmitWizardComplete = (payload: NerApplicationSubmitRequestTaskPayload): boolean => {
  if (payload?.payloadType === 'NER_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD') {
    return submitVerificationWizardComplete(payload);
  }
  const { nerSectionsCompleted } = payload || {};
  return nerSectionsCompleted?.['details'];
};

export const nerTaskCompleted = (ner: NER) => {
  const { nerFiles, mmpFiles } = ner || {};

  return nerFiles?.file && mmpFiles?.file;
};

export function submitVerificationWizardComplete(payload: NERApplicationVerificationSubmitRequestTaskPayload): boolean {
  return !!(
    payload?.nerSectionsCompleted?.['opinionStatement'] === true &&
    payload?.nerSectionsCompleted?.['overallDecision'] === true
  );
}
