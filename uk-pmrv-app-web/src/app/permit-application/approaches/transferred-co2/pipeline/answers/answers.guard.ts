import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Injectable()
export class AnswersGuard {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<true | UrlTree> {
    return this.store.pipe(
      map((permitState) => {
        const pipelineSystems = (
          permitState.permit.monitoringApproaches.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach
        )?.transportCO2AndN2OPipelineSystems;
        return (
          (permitState.permitSectionsCompleted?.TRANSFERRED_CO2_N2O_Pipeline?.[0] &&
            this.router.parseUrl(
              state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)).concat('summary'),
            )) ||
          pipelineSystems?.exist === false ||
          (pipelineSystems?.exist === true &&
            pipelineSystems?.procedureForLeakageEvents !== undefined &&
            pipelineSystems?.temperaturePressure !== undefined &&
            pipelineSystems?.proceduresForTransferredCO2AndN2O !== undefined &&
            pipelineSystems?.proceduresForTransferredCO2AndN2O?.locationOfRecords !== undefined) ||
          this.router.parseUrl(state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path) - 1))
        );
      }),
    );
  }
}
