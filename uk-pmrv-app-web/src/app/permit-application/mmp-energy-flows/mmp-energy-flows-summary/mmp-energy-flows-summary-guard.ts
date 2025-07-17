import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

@Injectable()
export class MMPEnergyFlowsSummaryGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((state) => {
          return (
            Object.keys(state?.permit?.monitoringMethodologyPlans?.digitizedPlan?.energyFlows || {})?.length === 4 ||
            this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/mmp-energy-flows`)
          );
        }),
      )
    );
  }
}
