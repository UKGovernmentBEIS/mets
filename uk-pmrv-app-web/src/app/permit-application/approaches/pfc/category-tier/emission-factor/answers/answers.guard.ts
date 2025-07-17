import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../../pfc-status';
import { isWizardComplete } from '../emission-factor-wizard';

@Injectable()
export class AnswersGuard {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return this.store.pipe(
      map((permitState) => {
        const index = route.paramMap.get('index');
        const wizardUrl = `/${this.store.urlRequestType}/${route.paramMap.get(
          'taskId',
        )}/pfc/category-tier/${index}/emission-factor`;
        const emissionFactor = (
          permitState.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach
        )?.sourceStreamCategoryAppliedTiers?.[index]?.emissionFactor;
        const status = categoryTierSubtaskStatus(permitState, route.data.statusKey, Number(index));

        return (
          (status === 'complete' && this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          isWizardComplete(emissionFactor) ||
          this.router.parseUrl(wizardUrl)
        );
      }),
    );
  }
}
