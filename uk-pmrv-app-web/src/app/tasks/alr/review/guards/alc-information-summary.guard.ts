import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AlrService } from '@tasks/alr/core';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const alcInformationSummaryGuard: CanActivateFn = (route, state) => {
  const alrService = inject(AlrService);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const payload: ALRApplicationRegulatorReviewSubmitRequestTaskPayload =
    alrService.payload() as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
  const alc = payload?.regulatorReviewOutcome;
  const sectionCompletedExist =
    payload?.regulatorReviewSectionsCompleted?.['ALC'] !== undefined &&
    payload?.regulatorReviewSectionsCompleted?.['ALC'] !== null;

  return (
    (alc.conservativeDeterminesActivity !== null &&
      alc.conservativeDeterminesActivity !== undefined &&
      sectionCompletedExist) ||
    router.parseUrl(baseUrl)
  );
};
