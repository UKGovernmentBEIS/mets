import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { MeasurementOfCO2MonitoringApproach, MeasurementOfN2OMonitoringApproach } from 'pmrv-api';

import { MEASUREMENTCategoryTierSubtaskStatus } from '../../../approaches/measurement/measurement-status';
import { N2OCategoryTierSubtaskStatus } from '../../../approaches/n2o/n2o-status';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { isWizardComplete } from '../emissions-wizard';

@Injectable()
export class AnswersGuard implements CanActivate {
  constructor(
    private readonly router: Router,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((permitState) => {
        const { taskKey } = route.data;
        const index = route.paramMap.get('index');

        const wizardUrl = `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/${
          taskKey === 'MEASUREMENT_CO2' ? 'measurement' : 'nitrous-oxide'
        }/category-tier/${index}/emissions`;

        const measuredEmissions = (
          permitState.permit.monitoringApproaches[taskKey] as
            | MeasurementOfN2OMonitoringApproach
            | MeasurementOfCO2MonitoringApproach
        )?.emissionPointCategoryAppliedTiers?.[index]?.measuredEmissions;
        const taskStatus =
          taskKey === 'MEASUREMENT_CO2'
            ? MEASUREMENTCategoryTierSubtaskStatus(permitState, 'MEASUREMENT_CO2_Measured_Emissions', Number(index))
            : N2OCategoryTierSubtaskStatus(permitState, 'MEASUREMENT_N2O_Measured_Emissions', Number(index));

        return (
          (taskStatus === 'complete' && this.router.parseUrl(wizardUrl.concat('/summary'))) ||
          isWizardComplete(taskKey, measuredEmissions) ||
          this.router.parseUrl(wizardUrl)
        );
      }),
    );
  }
}
