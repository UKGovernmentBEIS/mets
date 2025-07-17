import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable()
export class UploadFileGuard {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
          const monitoringMethodologyPlans = permitState.permit?.monitoringMethodologyPlans;
          const digitizedMmp = permitState.features?.['digitized-mmp'];

          if (digitizedMmp === true) {
            return (
              (permitState.permitSectionsCompleted?.monitoringMethodologyPlans?.[0] &&
                this.router.parseUrl(baseUrl.concat('/summary'))) ||
              (monitoringMethodologyPlans?.exist === undefined && this.router.parseUrl(baseUrl)) ||
              ((monitoringMethodologyPlans?.exist === true || monitoringMethodologyPlans?.exist === false) &&
                this.router.parseUrl(baseUrl.concat('/answers')))
            );
          } else {
            return (
              (permitState.permitSectionsCompleted?.monitoringMethodologyPlans?.[0] &&
                this.router.parseUrl(baseUrl.concat('/summary'))) ||
              (monitoringMethodologyPlans?.exist === undefined && this.router.parseUrl(baseUrl)) ||
              (monitoringMethodologyPlans?.exist === true && !monitoringMethodologyPlans?.plans) ||
              (monitoringMethodologyPlans?.exist === true &&
                monitoringMethodologyPlans?.plans &&
                this.router.parseUrl(baseUrl.concat('/answers'))) ||
              (monitoringMethodologyPlans?.exist === false && this.router.parseUrl(baseUrl.concat('/answers')))
            );
          }
        }),
      )
    );
  }
}
