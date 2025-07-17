import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';

import { BDRVerificationReturnedToOperatorRequestActionPayload } from 'pmrv-api';

export const returnToOperatorGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const changesRequired = (inject(BDR_TASK_FORM).value as BDRVerificationReturnedToOperatorRequestActionPayload)
    .changesRequired;

  return !!changesRequired || router.parseUrl(baseUrl);
};
