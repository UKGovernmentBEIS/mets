import { Inject, Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { Observable } from 'rxjs';

import { LegalEntitiesService } from 'pmrv-api';

import { LEGAL_ENTITY_FORM_OP } from '../../factories/legal-entity/legal-entity-form-op.factory';
import { LegalEntitySelectGuard } from '../../guards/legal-entity-select.guard';
import { InstallationAccountApplicationStore } from '../../store/installation-account-application.store';

@Injectable()
export class LegalEntitySelectOpGuard extends LegalEntitySelectGuard {
  constructor(
    @Inject(LEGAL_ENTITY_FORM_OP) form: UntypedFormGroup,
    legalEntitiesService: LegalEntitiesService,
    router: Router,
    store: InstallationAccountApplicationStore,
  ) {
    super(form, legalEntitiesService, router, store);
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return super.canActivate(route, state, 'regno');
  }
}
