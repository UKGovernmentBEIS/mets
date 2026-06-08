import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { wizardIsCompleted } from '@tasks/ner/utils';

import { NerService } from '..';

export const wizardStepGuard: CanActivateFn = (route, state) => {
  const nerService = inject(NerService);
  const payload = nerService.payload();
  const router = inject(Router);
  const urlSplit = router.currentNavigation().finalUrl.toString().split('/');
  const isWizardCompleted = wizardIsCompleted(payload, urlSplit[5]);
  const baseUrl = `tasks/${route.paramMap.get('taskId')}/ner/${urlSplit[4]}`;
  const summaryUrl = `/${baseUrl}/${urlSplit[5]}/summary`;
  const wizardFirstStep = `/${baseUrl}/${urlSplit[5]}`;
  const isCurrentSummaryPage = state.url.includes(summaryUrl);

  return (
    router.currentNavigation().extras?.state?.changing ||
    (!isWizardCompleted && !isCurrentSummaryPage) ||
    (!isWizardCompleted && isCurrentSummaryPage && router.parseUrl(wizardFirstStep)) ||
    (isWizardCompleted && !isCurrentSummaryPage && router.parseUrl(summaryUrl)) ||
    true
  );
};
