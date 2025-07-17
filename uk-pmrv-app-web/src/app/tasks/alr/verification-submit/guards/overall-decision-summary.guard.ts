import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { overallDecisionWizardCompleted } from '@shared/components/overall-decision-summary-template/overall-decision';
import { AlrService } from '@tasks/alr/core';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const overallDecisionSummaryGuard: CanActivateFn = (route, state) => {
  const alrService = inject(AlrService);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const payload: ALRApplicationVerificationSubmitRequestTaskPayload = alrService.payload();
  const overallAssessment = payload.verificationReport.overallAssessment;

  return overallDecisionWizardCompleted(overallAssessment) || router.parseUrl(baseUrl);
};
