import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasurementOfN2OEmissionPointCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Injectable()
export class DeleteGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store
      .findTask<MeasurementOfN2OEmissionPointCategoryAppliedTier[]>(
        'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
      )
      .pipe(
        map(
          (tiers) =>
            (tiers && !!tiers[Number(route.paramMap.get('index'))]) ||
            this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/nitrous-oxide`),
        ),
      );
  }
}
