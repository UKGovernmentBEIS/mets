import { Inject, Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { LEGAL_ENTITY_FORM_OP } from '../../factories/legal-entity/legal-entity-form-op.factory';

@Injectable()
export class LegalEntityRegnoOpGuard {
  constructor(
    private readonly router: Router,
    @Inject(LEGAL_ENTITY_FORM_OP) private readonly form: UntypedFormGroup,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    if (this.form.get('selectGroup').get('isNew').value) {
      return true;
    } else {
      const urlSegments = state.url.split('/');
      const newUrl = urlSegments.slice(0, -2).join('/');
      return this.router.parseUrl(newUrl);
    }
  }
}
