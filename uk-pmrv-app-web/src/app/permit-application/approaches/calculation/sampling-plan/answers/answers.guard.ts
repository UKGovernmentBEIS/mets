import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Injectable()
export class AnswersGuard implements CanActivate {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<true | UrlTree> {
    return this.store.pipe(
      map((permitState) => {
        const plan = (permitState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
          ?.samplingPlan;
        return (
          (permitState.permitSectionsCompleted?.CALCULATION_CO2_Plan?.[0] &&
            this.router.parseUrl(
              state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)).concat('summary'),
            )) ||
          plan?.exist === false ||
          (plan?.exist === true &&
            plan?.details?.procedurePlan !== undefined &&
            plan?.details?.appropriateness !== undefined &&
            plan?.details?.yearEndReconciliation !== undefined) ||
          this.router.parseUrl(state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path) - 1))
        );
      }),
    );
  }
}
