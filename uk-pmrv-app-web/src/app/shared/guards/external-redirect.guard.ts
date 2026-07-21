import { DOCUMENT } from '@angular/common';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn } from '@angular/router';

export const externalRedirectGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  inject(DOCUMENT).location.replace(route.data['externalUrl']);
  return false;
};
