import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { CalculationOfPfcEmissions } from 'pmrv-api';

import { isPfcWizardComplete } from '../../../../../shared/components/approaches/aer/monitoring-approaches.functions';
import { AerService } from '../../../core/aer.service';
import { sourceStreamEmissionStatus } from '../../../shared/components/submit/emissions-status';

@Injectable({
  providedIn: 'root',
})
export class WizardStepGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.aerService.getPayload().pipe(
        first(),
        map((payload) => {
          const index = Number(route.paramMap.get('index'));
          const taskId = route.paramMap.get('taskId');

          const sourceStreamEmission = (
            payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions
          )?.sourceStreamEmissions?.[index];

          const baseUrl = `tasks/${taskId}/aer/submit/pfc/${index}`;
          const wizardFirstStep = `/${baseUrl}/emission-network`;
          const summaryUrl = `/${baseUrl}/summary`;

          const needsReview = sourceStreamEmissionStatus('CALCULATION_PFC', payload, index) === 'needs review';
          const isCurrentStepFirstStep = state.url.includes('emission-network');

          return (
            (needsReview && isCurrentStepFirstStep) ||
            (needsReview && !isCurrentStepFirstStep && this.router.parseUrl(wizardFirstStep)) ||
            (!needsReview && !isPfcWizardComplete(sourceStreamEmission)) ||
            (!needsReview && isPfcWizardComplete(sourceStreamEmission) && this.router.parseUrl(summaryUrl))
          );
        }),
      )
    );
  }
}
