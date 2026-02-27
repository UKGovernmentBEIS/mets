import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { overallDecisionWizardCompleted } from '@shared/components/overall-decision-summary-template/overall-decision';
import { BdrS2Service } from '@tasks/bdrs2/core';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const OverallDecisionSummaryGuard: CanActivateFn = (route, state) => {
  const bdrs2Service = inject(BdrS2Service);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const payload: BDRS2ApplicationVerificationSubmitRequestTaskPayload = bdrs2Service.payload();
  const overallAssessment = payload.verificationReport.overallAssessment;

  return overallDecisionWizardCompleted(overallAssessment) || router.parseUrl(baseUrl);
};
