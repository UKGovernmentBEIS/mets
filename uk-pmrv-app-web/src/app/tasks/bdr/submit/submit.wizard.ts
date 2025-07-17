import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export function submitWizardComplete(payload: BDRApplicationSubmitRequestTaskPayload): boolean {
  if (payload?.payloadType === 'BDR_APPLICATION_AMENDS_SUBMIT_PAYLOAD') {
    return (
      payload?.bdrSectionsCompleted?.['baseline'] === true &&
      payload?.bdrSectionsCompleted?.['changesRequested'] === true
    );
  }

  return payload?.bdrSectionsCompleted?.['baseline'] === true;
}

export function baselineComplete(payload: BDRApplicationSubmitRequestTaskPayload): boolean {
  if (
    (!payload?.bdr?.isApplicationForFreeAllocation &&
      payload?.bdr?.statusApplicationType &&
      payload?.bdr?.infoIsCorrectChecked) ||
    (payload?.bdr?.isApplicationForFreeAllocation &&
      payload?.bdr?.statusApplicationType &&
      payload?.bdr?.infoIsCorrectChecked &&
      payload?.bdr?.hasMmp !== null &&
      payload?.bdr?.hasMmp !== undefined)
  ) {
    return true;
  }
  return false;
}

export const bdrSendReportBacklinkResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as BDRApplicationSubmitRequestTaskPayload;
  return payload?.bdr?.isApplicationForFreeAllocation ||
    (payload as any)?.regulatorReviewGroupDecisions?.BDR?.details?.verificationRequired === true ||
    payload?.verificationPerformed
    ? '../../'
    : '../';
};
