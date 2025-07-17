import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasurementOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';

@Injectable()
export class TransferredCO2DetailsGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const taskId = Number(route.paramMap.get('taskId'));
        const tiers = (state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach)
          ?.emissionPointCategoryAppliedTiers;
        return (
          tiers[index]?.emissionPointCategory?.transfer?.entryAccountingForTransfer ||
          this.router.parseUrl(`/${this.store.urlRequestType}/${taskId}/measurement/category-tier/${index}/category`)
        );
      }),
    );
  }
}
