import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { isCalculationWizardComplete } from '@shared/components/approaches/aer/monitoring-approaches.functions';

import { CalculationOfCO2Emissions } from 'pmrv-api';

import { AerService } from '../../../core/aer.service';
import { sourceStreamEmissionStatus } from '../../../shared/components/submit/emissions-status';

@Injectable({
  providedIn: 'root',
})
export class SummaryGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.aerService.getPayload().pipe(
        first(),
        map((payload) => {
          const index = Number(route.paramMap.get('index'));
          const taskId = route.paramMap.get('taskId');

          const hasTransfer = !!payload.aer.monitoringApproachEmissions.CALCULATION_CO2?.hasTransfer;

          const sourceStreamEmission = (
            payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions
          )?.sourceStreamEmissions?.[index];

          const baseUrl = `tasks/${taskId}/aer/submit/calculation-emissions/${index}`;

          const needsReview = sourceStreamEmissionStatus('CALCULATION_CO2', payload, index) === 'needs review';

          return (
            (isCalculationWizardComplete(sourceStreamEmission, hasTransfer) && !needsReview) ||
            this.router.parseUrl(baseUrl.concat('/emission-network'))
          );
        }),
      )
    );
  }
}
