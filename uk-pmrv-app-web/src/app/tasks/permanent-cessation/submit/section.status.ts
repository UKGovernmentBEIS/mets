import { PermanentCessation, PermanentCessationApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';

export const resolveSectionStatus = (
  payload: PermanentCessationApplicationSubmitRequestTaskPayload,
): TaskItemStatus => {
  return payload.permanentCessationSectionsCompleted.details
    ? 'complete'
    : !payload?.permanentCessation?.cessationScope
      ? 'not started'
      : 'in progress';
};

export const detailsWizardCompleted = (permanentCessation: PermanentCessation): boolean => {
  return (
    !!permanentCessation.additionalDetails &&
    !!permanentCessation.cessationDate &&
    !!permanentCessation.cessationScope &&
    !!permanentCessation.description
  );
};
