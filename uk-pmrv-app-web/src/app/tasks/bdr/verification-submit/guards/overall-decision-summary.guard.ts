import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { overallDecisionWizardCompleted } from '@shared/components/overall-decision-summary-template/overall-decision';
import { BdrService } from '@tasks/bdr/shared';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const overallDecisionSummaryGuard: CanActivateFn = (route, state) => {
  const bdrService = inject(BdrService);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const payload: BDRApplicationVerificationSubmitRequestTaskPayload = bdrService.payload();
  const overallAssessment = payload.verificationReport.overallAssessment;

  return overallDecisionWizardCompleted(overallAssessment) || router.parseUrl(baseUrl);
};
