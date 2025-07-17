import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

@Injectable()
export class ConnectionDetailsGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('monitoringMethodologyPlans').pipe(
      first(),
      map(
        (monitoringMethodologyPlans) =>
          !monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.connections ||
          monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.connections?.length < 10 ||
          this.router.parseUrl(
            `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/mmp-installation-description/summary`,
          ),
      ),
    );
  }
}
