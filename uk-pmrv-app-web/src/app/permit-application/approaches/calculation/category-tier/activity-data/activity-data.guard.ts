import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryTierSubtaskStatus } from '../../calculation-status';
import { isWizardComplete } from './activity-data-wizard';

@Injectable()
export class ActivityDataGuard implements CanActivate {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((permitState) => {
          const index = route.paramMap.get('index');
          const wizardUrl = `/${this.store.urlRequestType}/${route.paramMap.get(
            'taskId',
          )}/calculation/category-tier/${index}/activity-data`;
          const activityData = (
            permitState.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
          )?.sourceStreamCategoryAppliedTiers?.[index]?.activityData;
          const taskStatus = categoryTierSubtaskStatus(permitState, route.data.statusKey, Number(index));

          return (
            (taskStatus === 'complete' && this.router.parseUrl(wizardUrl.concat('/summary'))) ||
            (isWizardComplete(activityData) && this.router.parseUrl(wizardUrl.concat('/answers'))) ||
            true
          );
        }),
      )
    );
  }
}
