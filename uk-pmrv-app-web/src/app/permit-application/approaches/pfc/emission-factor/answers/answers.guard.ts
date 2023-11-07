import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { isWizardComplete } from '../emission-factor-wizard';

@Injectable()
export class AnswersGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const wizardUrl = `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/pfc/emission-factor`;
        const emissionFactor = (state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
          ?.tier2EmissionFactor;

        return (
          (state.permitSectionsCompleted?.CALCULATION_PFC_Tier2EmissionFactor?.[0] &&
            this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          isWizardComplete(emissionFactor) ||
          this.router.parseUrl(wizardUrl)
        );
      }),
    );
  }
}
