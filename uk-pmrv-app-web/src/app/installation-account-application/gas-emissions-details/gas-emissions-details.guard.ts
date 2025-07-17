import { Inject, Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';

@Injectable()
export class GasEmissionsDetailsGuard {
  constructor(
    private readonly router: Router,
    @Inject(INSTALLATION_FORM) private readonly form: UntypedFormGroup,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    return (
      (this.form.get('installationTypeGroup').get('type').value === 'ONSHORE' && this.form.get('onshoreGroup').valid) ||
      this.router.parseUrl(state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)).concat('type'))
    );
  }
}
