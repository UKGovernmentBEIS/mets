import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable()
export class PipelineGuard {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const pipelineSystems = (
            permitState.permit.monitoringApproaches.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach
          )?.transportCO2AndN2OPipelineSystems;

          return (
            (permitState.permitSectionsCompleted?.TRANSFERRED_CO2_N2O_Pipeline?.[0] &&
              this.router.parseUrl(state.url.concat('/summary'))) ||
            ((pipelineSystems?.exist === false ||
              (pipelineSystems?.procedureForLeakageEvents !== undefined &&
                pipelineSystems?.proceduresForTransferredCO2AndN2O !== undefined &&
                pipelineSystems?.temperaturePressure !== undefined)) &&
              this.router.parseUrl(state.url.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
