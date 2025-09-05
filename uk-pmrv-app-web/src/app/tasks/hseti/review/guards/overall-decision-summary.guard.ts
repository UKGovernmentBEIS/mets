import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { HseTiService } from '@tasks/hseti/core';
import { isOverallDecisionPopulated } from '@tasks/hseti/utils';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, HSETIRegulatorReviewOverallDecision } from 'pmrv-api';

export const HsetiOverallDecisionSummaryGuard: CanActivateFn = (route, state) => {
  const hsetiService = inject(HseTiService);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

  const payload = hsetiService.payload() as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
  const overallDecision = payload?.overallDecision as HSETIRegulatorReviewOverallDecision;
  const sectionCompletedExist = overallDecision?.type !== undefined && overallDecision?.type !== null;
  return (isOverallDecisionPopulated(overallDecision) && sectionCompletedExist) || router.parseUrl(baseUrl);
};
