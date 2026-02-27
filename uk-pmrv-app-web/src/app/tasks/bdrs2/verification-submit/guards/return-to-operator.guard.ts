import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';

import { BDRS2VerificationReturnedToOperatorRequestActionPayload } from 'pmrv-api';

export const returnToOperatorGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const changesRequired = (inject(BDRS2_TASK_FORM).value as BDRS2VerificationReturnedToOperatorRequestActionPayload)
    .changesRequired;

  return !!changesRequired || router.parseUrl(baseUrl);
};
