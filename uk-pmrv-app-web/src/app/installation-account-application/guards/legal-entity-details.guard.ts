import { Inject, Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { LEGAL_ENTITY_FORM_REG } from '../factories/legal-entity/legal-entity-form-reg.factory';

@Injectable()
export class LegalEntityDetailsGuard {
  constructor(
    private readonly router: Router,
    @Inject(LEGAL_ENTITY_FORM_REG) private readonly form: UntypedFormGroup,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    return (
      this.form.get('selectGroup').get('isNew').value ||
      this.router.parseUrl(state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)) + 'select')
    );
  }
}
