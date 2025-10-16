import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { map } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

import { PermitTransferAService } from './permit-transfer-a.service';

export const getSectionStatus = (payload: PermitTransferAApplicationRequestTaskPayload): TaskItemStatus => {
  const firstStepIsCompleted = !!payload?.reason;

  return payload?.sectionCompleted ? 'complete' : firstStepIsCompleted ? 'in progress' : 'not started';
};

export const isWizardCompleted = (payload: PermitTransferAApplicationRequestTaskPayload, isAlrVisible: boolean) => {
  return (
    !!payload.reason &&
    !!payload.transferDate &&
    !!payload.payer &&
    !!payload.aerLiable &&
    (isAlrVisible ? !!payload.alrLiable : true) &&
    !!payload.transferCode
  );
};

export const codeBacklinkResolver: ResolveFn<string> = () => {
  const permitTransferAService = inject(PermitTransferAService);

  return permitTransferAService.isAlrVisible$.pipe(
    map((isFinalAlrVisible) => (isFinalAlrVisible ? '../activity-level-report' : '../aem-report')),
  );
};
