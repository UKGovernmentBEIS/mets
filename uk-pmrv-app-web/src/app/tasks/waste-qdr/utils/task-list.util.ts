import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { RequestTaskDTO, WasteQDR, WasteQDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const wasteQdrWaitTasks: Array<RequestTaskDTO['type']> = [];

export const wasteQdrWarningText: Partial<Record<RequestTaskDTO['type'], string>> = {};

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

export const wasteQdrSubmitWizardComplete = (payload: WasteQDRApplicationSubmitRequestTaskPayload): boolean => {
  return payload?.wasteQDRSectionsCompleted?.['qdr'] === true;
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
