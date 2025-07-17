import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { BdrService } from '@tasks/bdr/shared';

import { BDRApplicationRegulatorReviewOutcome, BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

const outcomeSectionsComplete = (outcome: BDRApplicationRegulatorReviewOutcome) => {
  return (
    outcome?.hasRegulatorSentFreeAllocation !== undefined &&
    outcome?.hasRegulatorSentHSE !== undefined &&
    outcome?.hasRegulatorSentUSE !== undefined
  );
};

export const outcomeSummaryGuard: CanActivateFn = (route, state) => {
  const bdrService = inject(BdrService);
  const router = inject(Router);
  const payload = bdrService.payload() as BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
  const outcomeComplete = payload.regulatorReviewSectionsCompleted['outcome'] === true;
  return (
    outcomeComplete ||
    outcomeSectionsComplete(payload.regulatorReviewOutcome) ||
    router.parseUrl(state.url + '/fa-decision')
  );
};
