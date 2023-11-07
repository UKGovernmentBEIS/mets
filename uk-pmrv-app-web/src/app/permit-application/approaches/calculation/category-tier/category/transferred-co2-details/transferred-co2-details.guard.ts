import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';

@Injectable()
export class TransferredCO2DetailsGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const taskId = Number(route.paramMap.get('taskId'));
        const tiers = (state.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers;
        return (
          tiers[index]?.sourceStreamCategory?.transfer?.entryAccountingForTransfer ||
          this.router.parseUrl(`/${this.store.urlRequestType}/${taskId}/calculation/category-tier/${index}/category`)
        );
      }),
    );
  }
}
