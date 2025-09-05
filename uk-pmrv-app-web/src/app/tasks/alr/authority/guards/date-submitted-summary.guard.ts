import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

import { AlrService } from '../../core';
import { isAuthorityDateSubmittedWizardCompleted } from '../../utils';

export const dateSubmittedSummaryGuard: CanActivateFn = (route, state) => {
  const alrService = inject(AlrService);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const payload = alrService.payload() as ALRAuthorityResponseSubmitRequestTaskPayload;

  const sectionCompletedExist =
    payload?.authorityReviewSectionsCompleted?.['applicationSubmitted'] !== undefined &&
    payload?.authorityReviewSectionsCompleted?.['applicationSubmitted'] !== null;

  return (
    (isAuthorityDateSubmittedWizardCompleted(payload.authorityReviewOutcome) && sectionCompletedExist) ||
    router.parseUrl(`${baseUrl}/date`)
  );
};
