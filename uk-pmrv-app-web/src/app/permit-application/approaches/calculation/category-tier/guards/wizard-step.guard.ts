import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { statusKeyTosubtaskUrlParamMapper } from '../category-tier';
import { isWizardComplete } from '../category-tier-wizard';

@Injectable()
export class WizardStepGuard implements CanActivate {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((state) => {
          const index = Number(route.paramMap.get('index'));
          const taskId = route.paramMap.get('taskId');
          const statusKey = route.data.statusKey;
          const subtaskUrlParam = statusKeyTosubtaskUrlParamMapper[statusKey];

          const wizardUrl = `/${this.store.urlRequestType}/${taskId}/calculation/category-tier/${index}/${subtaskUrlParam}`;

          return !isWizardComplete(state, index, statusKey) || this.router.parseUrl(wizardUrl.concat('/answers'));
        }),
      )
    );
  }
}
