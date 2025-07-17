import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasurementOfCO2EmissionPointCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable()
export class CategoryTierGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const index = Number(route.paramMap.get('index'));
    return this.store
      .findTask<
        MeasurementOfCO2EmissionPointCategoryAppliedTier[]
      >('monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers')
      .pipe(
        map(
          (tiers) =>
            (tiers && (!!tiers[index] || tiers.length === index)) ||
            (!tiers && index === 0) ||
            this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/measurement`),
        ),
      );
  }
}
