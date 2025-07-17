import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { isProductBenchmarkComplete } from '../mmp-sub-installations-status';

@Injectable()
export class MMPSubInstallationsStepGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const subInstallationNo = Number(route.paramMap.get('subInstallationNo'));

    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((state) => {
          return (
            !isProductBenchmarkComplete(
              state?.permit?.monitoringMethodologyPlans?.digitizedPlan?.subInstallations?.[subInstallationNo],
            ) ||
            this.router.parseUrl(
              `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/mmp-sub-installations/${subInstallationNo}/summary`,
            )
          );
        }),
      )
    );
  }
}
