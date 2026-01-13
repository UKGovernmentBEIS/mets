import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { qdrTaskCompleted } from '@tasks/waste-qdr/utils';

import { WasteQdrService } from '../waste-qdr.service';

export const wizardStepGuard: CanActivateFn = (route, state) => {
  const wasteQdrService = inject(WasteQdrService);
  const qdr = wasteQdrService.payload().qdr;
  const router = inject(Router);
  const wizardIsCompleted = qdrTaskCompleted(qdr);
  const baseUrl = `tasks/${route.paramMap.get('taskId')}/waste-qdr/submit`;
  const summaryUrl = `/${baseUrl}/qdr/summary`;
  const wizardFirstStep = `/${baseUrl}/qdr`;
  const isCurrentSummaryPage = state.url.includes(summaryUrl);

  return (
    router.getCurrentNavigation().extras?.state?.changing ||
    (!wizardIsCompleted && !isCurrentSummaryPage) ||
    (!wizardIsCompleted && isCurrentSummaryPage && router.parseUrl(wizardFirstStep)) ||
    (wizardIsCompleted && !isCurrentSummaryPage && router.parseUrl(summaryUrl)) ||
    true
  );
};
