import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { FallbackMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { FALLBACKCategoryTierSubtaskStatus } from '../../../fallback-status';

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
        const tiers = (state.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach)
          ?.sourceStreamCategoryAppliedTiers;

        return (
          (tiers &&
            !!tiers[index] &&
            FALLBACKCategoryTierSubtaskStatus(state, 'FALLBACK_Category', index) === 'complete') ||
          this.router.parseUrl(
            `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/fall-back/category-tier/${index}/category`,
          )
        );
      }),
    );
  }
}
