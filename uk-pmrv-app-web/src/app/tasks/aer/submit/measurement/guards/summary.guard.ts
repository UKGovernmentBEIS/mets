import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { isMeasurementWizardComplete } from '../../../../../shared/components/approaches/aer/monitoring-approaches.functions';
import { AerService } from '../../../core/aer.service';
import { emissionPointEmissionsStatus } from '../measurement-status';

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
          const taskKey = route.data.taskKey;

          const hasTransfer = !!payload.aer.monitoringApproachEmissions[taskKey]?.hasTransfer;

          const sourceStreamEmission = (payload.aer.monitoringApproachEmissions[taskKey] as any)
            ?.emissionPointEmissions?.[index];

          const baseUrl = `tasks/${taskId}/aer/submit/${
            taskKey === 'MEASUREMENT_CO2' ? 'measurement-co2' : 'measurement-n2o'
          }/${index}`;

          const needsReview = emissionPointEmissionsStatus(taskKey, payload, index) === 'needs review';

          return (
            (isMeasurementWizardComplete(sourceStreamEmission, hasTransfer) && !needsReview) ||
            this.router.parseUrl(baseUrl.concat('/emission-network'))
          );
        }),
      )
    );
  }
}
