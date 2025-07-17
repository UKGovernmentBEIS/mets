import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AlrService } from '@tasks/alr/core';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const opinionStatementSummaryGuard: CanActivateFn = (route, state) => {
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const alrService = inject(AlrService);
  const payload: ALRApplicationVerificationSubmitRequestTaskPayload = alrService.payload();
  const isEditable = alrService.isEditable();
  const router = inject(Router);

  return (
    !isEditable ||
    (payload?.verificationReport?.opinionStatement?.opinionStatementFiles &&
      payload?.verificationReport?.opinionStatement?.opinionStatementFiles?.length > 0) ||
    router.parseUrl(baseUrl)
  );
};
