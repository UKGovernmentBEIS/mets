import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { TASKS_RETURN_TO_OPERATOR_FORM } from '../core';

export const tasksReturnToOperatorGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
  const changesRequired = inject(TASKS_RETURN_TO_OPERATOR_FORM).value.changesRequired;

  return !!changesRequired || router.parseUrl(baseUrl);
};
