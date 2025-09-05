import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AlrService } from '@tasks/alr/core';
import { isDeterminationPopulated } from '@tasks/alr/utils';

import {
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRClosedDetermination,
  DoalProceedToAuthorityDetermination,
} from 'pmrv-api';

export const alrDeterminationSummaryGuard: CanActivateFn = (route, state) => {
  const alrService = inject(AlrService);
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

  const payload: ALRApplicationRegulatorReviewSubmitRequestTaskPayload =
    alrService.payload() as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
  const determination = payload?.regulatorReviewOutcome?.determination as
    | DoalProceedToAuthorityDetermination
    | ALRClosedDetermination;
  const sectionCompletedExist =
    payload?.regulatorReviewSectionsCompleted?.['DETERMINATION'] !== undefined &&
    payload?.regulatorReviewSectionsCompleted?.['DETERMINATION'] !== null;

  return (isDeterminationPopulated(determination) && sectionCompletedExist) || router.parseUrl(baseUrl);
};
