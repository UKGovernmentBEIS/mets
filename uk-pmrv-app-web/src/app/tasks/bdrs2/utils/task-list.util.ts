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

export const bdrs2SendReportBacklinkResolver: ResolveFn<string> = () => {
  const payload = inject(CommonTasksStore).getValue().requestTaskItem.requestTask
    .payload as BDRS2ApplicationSubmitRequestTaskPayload;

  const sendToVerifierOrRegulatorCondition =
    (payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'CONTINUE_AS_HSE' ||
      payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType ===
        'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT') &&
    (!payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector ||
      (payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector &&
        !payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam)) &&
    payload?.bdrs2?.bdrs2Files?.file;

  const isCbam = payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam;
  const verificationRequiredFromAmends =
    (payload as any)?.regulatorReviewGroupDecisions?.BDRS2?.details?.verificationRequired === true && isCbam;

  return verificationRequiredFromAmends ||
    payload?.verificationPerformed ||
    (!payload?.verificationPerformed && !sendToVerifierOrRegulatorCondition)
    ? '../../'
    : '../';
};

export function baselineComplete(payload: BDRS2ApplicationSubmitRequestTaskPayload): boolean {
  if (
    payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'WITHDRAW' ||
    ((payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType ===
      'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT' ||
      payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'CONTINUE_AS_HSE') &&
      payload?.bdrs2?.bdrs2guardQuestions?.covidAdjustments !== null &&
      payload?.bdrs2?.bdrs2guardQuestions?.covidAdjustments !== undefined &&
      payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector === false &&
      payload?.bdrs2?.bdrs2Files?.file) ||
    ((payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType ===
      'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT' ||
      payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'CONTINUE_AS_HSE') &&
      payload?.bdrs2?.bdrs2guardQuestions?.covidAdjustments !== null &&
      payload?.bdrs2?.bdrs2guardQuestions?.covidAdjustments !== undefined &&
      payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector &&
      payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam === true &&
      payload?.bdrs2?.bdrs2Files?.file &&
      payload?.bdrs2?.mmpFiles?.file) ||
    ((payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType ===
      'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT' ||
      payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'CONTINUE_AS_HSE') &&
      payload?.bdrs2?.bdrs2guardQuestions?.covidAdjustments !== null &&
      payload?.bdrs2?.bdrs2guardQuestions?.covidAdjustments !== undefined &&
      payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector &&
      payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam === false &&
      payload?.bdrs2?.bdrs2Files?.file)
  ) {
    return true;
  }
  return false;
}

export function submitWizardComplete(payload: BDRS2ApplicationSubmitRequestTaskPayload): boolean {
  if (payload?.payloadType === 'BDRS2_APPLICATION_AMENDS_SUBMIT_PAYLOAD') {
    return (
      payload?.bdrs2SectionsCompleted?.['baseline'] === true &&
      payload?.bdrs2SectionsCompleted?.['changesRequested'] === true
    );
  }

  return payload?.bdrs2SectionsCompleted?.['baseline'] === true;
}
