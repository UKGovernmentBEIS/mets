import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { isPfcWizardComplete } from '@shared/components/approaches/aer/monitoring-approaches.functions';

import { CalculationOfPfcEmissions } from 'pmrv-api';

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

          const sourceStreamEmission = (
            payload.aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions
          )?.sourceStreamEmissions?.[index];

          const baseUrl = `tasks/${taskId}/aer/submit/pfc/${index}`;

          const needsReview = sourceStreamEmissionStatus('CALCULATION_PFC', payload, index) === 'needs review';

          return (
            (isPfcWizardComplete(sourceStreamEmission) && !needsReview) ||
            this.router.parseUrl(baseUrl.concat('/emission-network'))
          );
        }),
      )
    );
  }
}
