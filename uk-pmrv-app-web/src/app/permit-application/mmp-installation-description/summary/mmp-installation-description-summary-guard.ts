import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

@Injectable()
export class MMPInstallationDescriptionSummaryGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        return (
          (state?.permit?.monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.description &&
            state?.permit?.monitoringMethodologyPlans?.digitizedPlan?.installationDescription?.flowDiagrams?.length >
              0) ||
          this.router.parseUrl(
            `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/mmp-installation-description`,
          )
        );
      }),
    );
  }
}
