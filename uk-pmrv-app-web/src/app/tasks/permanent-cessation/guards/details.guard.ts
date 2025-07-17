import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { PermanentCessationService } from '../shared';
import { detailsWizardCompleted } from '../submit/section.status';

export const permanentCessationDetailsSummaryGuard: CanActivateFn = (route, state) => {
  const permanentCessationService = inject(PermanentCessationService);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const permanentCessation = permanentCessationService.payload().permanentCessation;

  return detailsWizardCompleted(permanentCessation) || router.parseUrl(baseUrl);
};

export const permanentCessationDetailsGuard: CanActivateFn = (_, state) => {
  const permanentCessationService = inject(PermanentCessationService);
  const router = inject(Router);
  const permanentCessation = permanentCessationService.payload().permanentCessation;

  return (
    router.getCurrentNavigation().extras?.state?.changing ||
    !detailsWizardCompleted(permanentCessation) ||
    router.parseUrl(`${state.url}/summary`)
  );
};
