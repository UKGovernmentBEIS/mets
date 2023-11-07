import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationOfPFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../../pfc-status';
import { isWizardComplete } from '../activity-data-wizard';

@Injectable()
export class AnswersGuard implements CanActivate {
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
        )}/pfc/category-tier/${index}/activity-data`;
        const activityData = (
          permitState.permit.monitoringApproaches.CALCULATION_PFC as CalculationOfPFCMonitoringApproach
        )?.sourceStreamCategoryAppliedTiers?.[index]?.activityData;
        const taskStatus = categoryTierSubtaskStatus(permitState, route.data.statusKey, Number(index));

        return (
          (taskStatus === 'complete' && this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          isWizardComplete(activityData) ||
          this.router.parseUrl(wizardUrl)
        );
      }),
    );
  }
}
