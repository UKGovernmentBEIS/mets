import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../../pfc-status';

@Injectable()
export class CategorySummaryGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const index = Number(route.paramMap.get('index'));
        const tiers = (state.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers;

        return (
          (tiers &&
            !!tiers[index] &&
            categoryTierSubtaskStatus(state, 'CALCULATION_PFC_Category', index) === 'complete') ||
          this.router.parseUrl(
            `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/pfc/category-tier/${index}/category`,
          )
        );
      }),
    );
  }
}
