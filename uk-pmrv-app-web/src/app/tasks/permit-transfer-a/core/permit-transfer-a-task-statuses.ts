import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

export const getSectionStatus = (payload: PermitTransferAApplicationRequestTaskPayload): TaskItemStatus => {
  const firstStepIsCompleted = !!payload?.reason;
  return payload?.sectionCompleted ? 'complete' : firstStepIsCompleted ? 'in progress' : 'not started';
};

export const isWizardCompleted = (payload: PermitTransferAApplicationRequestTaskPayload) => {
  return !!payload.reason && !!payload.transferDate && !!payload.payer && !!payload.aerLiable && !!payload.transferCode;
};
