import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { isCalculationWizardComplete } from '@shared/components/approaches/aer/monitoring-approaches.functions';

import { CalculationOfCO2Emissions } from 'pmrv-api';

import { AerService } from '../../../core/aer.service';
import { sourceStreamEmissionStatus } from '../../../shared/components/submit/emissions-status';

@Injectable({
  providedIn: 'root',
})
export class WizardStepGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
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
          const wizardFirstStep = `/${baseUrl}/emission-network`;
          const summaryUrl = `/${baseUrl}/summary`;

          const needsReview = sourceStreamEmissionStatus('CALCULATION_CO2', payload, index) === 'needs review';
          const isCurrentStepFirstStep = state.url.includes('emission-network');

          return (
            (needsReview && isCurrentStepFirstStep) ||
            (needsReview && !isCurrentStepFirstStep && this.router.parseUrl(wizardFirstStep)) ||
            (!needsReview && !isCalculationWizardComplete(sourceStreamEmission, hasTransfer)) ||
            (!needsReview &&
              isCalculationWizardComplete(sourceStreamEmission, hasTransfer) &&
              this.router.parseUrl(summaryUrl))
          );
        }),
      )
    );
  }
}
