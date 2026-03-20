import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { BdrS2Service } from '@tasks/bdrs2/core';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

const outcomeSectionsComplete = (payload: BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload) => {
  const isWithdrawn = payload.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'WITHDRAW';
  const hasCBAM = payload.bdrs2?.bdrs2guardQuestions?.inEiteSector === true;
  const outcome = payload.regulatorReviewOutcome;

  return isWithdrawn
    ? outcome?.freeAllocationOpinion !== undefined &&
        outcome?.freeAllocationOpinion !== null &&
        (outcome?.covidAdjustmentsOpinion === undefined || outcome?.covidAdjustmentsOpinion === null) &&
        (outcome?.installationSectorOpinion === undefined || outcome?.installationSectorOpinion === null) &&
        (outcome?.cbamSplitOpinion === undefined || outcome?.cbamSplitOpinion === null) &&
        (outcome.file === undefined || outcome.file === null)
    : hasCBAM
      ? outcome?.freeAllocationOpinion !== undefined &&
        outcome?.freeAllocationOpinion !== null &&
        outcome?.covidAdjustmentsOpinion !== undefined &&
        outcome?.covidAdjustmentsOpinion !== null &&
        outcome?.installationSectorOpinion !== undefined &&
        outcome?.installationSectorOpinion !== null &&
        outcome?.cbamSplitOpinion !== undefined &&
        outcome?.cbamSplitOpinion !== null
      : outcome?.freeAllocationOpinion !== undefined &&
        outcome?.freeAllocationOpinion !== null &&
        outcome?.covidAdjustmentsOpinion !== undefined &&
        outcome?.covidAdjustmentsOpinion !== null &&
        outcome?.installationSectorOpinion !== undefined &&
        outcome?.installationSectorOpinion !== null &&
        (outcome?.cbamSplitOpinion === undefined || outcome?.cbamSplitOpinion === null);
};

export const outcomeSummaryGuard: CanActivateFn = (route, state) => {
  const bdrs2Service = inject(BdrS2Service);
  const router = inject(Router);
  const payload = bdrs2Service.payload() as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
  const outcomeComplete = payload.regulatorReviewSectionsCompleted['outcome'] === true;
  return (
    outcomeComplete || outcomeSectionsComplete(payload) || router.parseUrl(state.url.split('/').slice(0, -1).join('/'))
  );
};
