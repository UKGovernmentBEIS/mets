import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { isWizardComplete } from '../inherent-co2-wizard';

@Injectable({
  providedIn: 'root',
})
export class WizardStepGuard {
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

          const listUrl = `/${this.store.urlRequestType}/${taskId}/inherent-co2`;

          const installation = (state.permit.monitoringApproaches?.[statusKey] as InherentCO2MonitoringApproach)
            ?.inherentReceivingTransferringInstallations?.[index];

          return !isWizardComplete(installation) || this.router.parseUrl(listUrl);
        }),
      )
    );
  }
}
