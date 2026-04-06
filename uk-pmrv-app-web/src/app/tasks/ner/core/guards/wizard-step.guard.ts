import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { nerTaskCompleted } from '@tasks/ner/utils';

import { NerService } from '..';

export const wizardStepGuard: CanActivateFn = (route, state) => {
  const nerService = inject(NerService);
  const ner = nerService.payload().ner;
  const router = inject(Router);
  const wizardIsCompleted = nerTaskCompleted(ner);
  const baseUrl = `tasks/${route.paramMap.get('taskId')}/ner/submit`;
  const summaryUrl = `/${baseUrl}/details/summary`;
  const wizardFirstStep = `/${baseUrl}/details`;
  const isCurrentSummaryPage = state.url.includes(summaryUrl);

  return (
    router.currentNavigation().extras?.state?.changing ||
    (!wizardIsCompleted && !isCurrentSummaryPage) ||
    (!wizardIsCompleted && isCurrentSummaryPage && router.parseUrl(wizardFirstStep)) ||
    (wizardIsCompleted && !isCurrentSummaryPage && router.parseUrl(summaryUrl)) ||
    true
  );
};
