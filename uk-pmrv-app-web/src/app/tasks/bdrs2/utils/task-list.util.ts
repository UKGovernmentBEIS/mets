import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const bdrs2UploadFileBacklinkResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as BDRS2ApplicationSubmitRequestTaskPayload;
  const isEiteSector = payload.bdrs2?.bdrs2guardQuestions?.inEiteSector === true;

  return isEiteSector ? '../cbam' : '../details';
};

export function submitWizardComplete(payload: BDRS2ApplicationSubmitRequestTaskPayload): boolean {
  // if (payload?.payloadType === 'BDRS2_APPLICATION_AMENDS_SUBMIT_PAYLOAD') {
  //   return (
  //     payload?.bdrs2SectionsCompleted?.['activity'] === true &&
  //     payload?.bdrs2SectionsCompleted?.['changesRequested'] === true
  //   );
  // }

  return payload?.bdrs2SectionsCompleted?.['baseline'] === true;
}
