import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';

import { wizardIsCompleted } from '@tasks/ner/utils';

import { NerService } from '..';

export const wizardStepGuard: CanActivateFn = (route, state) => {
  const nerService = inject(NerService);
  const payload = nerService.payload();
  const router = inject(Router);

  const sectionRoute = route.parent ?? route;
  const wizardFirstStep = getFullPath(sectionRoute);
  const task = wizardFirstStep?.split('/')?.at(-1);
  const summaryUrl = `/${wizardFirstStep}/summary`;
  const isWizardCompleted = wizardIsCompleted(payload, task);
  const isCurrentSummaryPage = state.url.includes(summaryUrl);

  return (
    router.currentNavigation().extras?.state?.changing ||
    (!isWizardCompleted && !isCurrentSummaryPage) ||
    (!isWizardCompleted && isCurrentSummaryPage && router.parseUrl(wizardFirstStep)) ||
    (isWizardCompleted && !isCurrentSummaryPage && router.parseUrl(summaryUrl)) ||
    true
  );
};

const getFullPath = (route: ActivatedRouteSnapshot): string => route.pathFromRoot.flatMap(({ url }) => url).join('/');
