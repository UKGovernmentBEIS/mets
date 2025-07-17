import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { ALR_TASK_FORM } from '@tasks/alr/core';

import { ALRVerificationReturnedToOperatorRequestActionPayload } from 'pmrv-api';

export const returnToOperatorGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const changesRequired = (inject(ALR_TASK_FORM).value as ALRVerificationReturnedToOperatorRequestActionPayload)
    .changesRequired;

  return !!changesRequired || router.parseUrl(baseUrl);
};
