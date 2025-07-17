import { Inject, Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { Observable } from 'rxjs';

import { LegalEntitiesService } from 'pmrv-api';

import { LEGAL_ENTITY_FORM_REG } from '../../factories/legal-entity/legal-entity-form-reg.factory';
import { LegalEntitySelectGuard } from '../../guards/legal-entity-select.guard';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';

@Injectable()
export class LegalEntitySelectRegGuard extends LegalEntitySelectGuard {
  constructor(
    @Inject(LEGAL_ENTITY_FORM_REG) form: UntypedFormGroup,
    legalEntitiesService: LegalEntitiesService,
    router: Router,
    store: InstallationAccountApplicationStore,
  ) {
    super(form, legalEntitiesService, router, store);
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return super.canActivate(route, state, 'details');
  }
}
