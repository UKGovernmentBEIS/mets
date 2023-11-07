import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { N2OCategoryTierSubtaskStatus } from '../../../n2o-status';

@Injectable()
export class CategorySummaryGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const taskId = Number(route.paramMap.get('taskId'));
        const tiers = (state.permit.monitoringApproaches.MEASUREMENT_N2O as MeasurementOfN2OMonitoringApproach)
          ?.emissionPointCategoryAppliedTiers;

        return (
          (tiers &&
            !!tiers[index] &&
            N2OCategoryTierSubtaskStatus(state, 'MEASUREMENT_N2O_Category', index) === 'complete') ||
          this.router.parseUrl(`/${this.store.urlRequestType}/${taskId}/nitrous-oxide/category-tier/${index}/category`)
        );
      }),
    );
  }
}
