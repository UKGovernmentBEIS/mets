import { ChangeDetectionStrategy, Component } from '@angular/core';

import { filter, map } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfPfcEmissions } from 'pmrv-api';

import { AerService } from '../../core/aer.service';
import { sourceStreamEmissionStatus } from '../../shared/components/submit/emissions-status';

@Component({
  selector: 'app-pfc',
  templateUrl: './pfc.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PfcComponent {
  isEditable$ = this.aerService.isEditable$;
  aer$ = this.aerService.getPayload().pipe(
    filter((payload) => !!payload),
    map((payload) => (payload as AerApplicationSubmitRequestTaskPayload).aer),
  );

  sourceStreamEmissions$ = this.aer$.pipe(
    map((aer) => (aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)?.sourceStreamEmissions),
  );

  statuses$ = this.aerService.getPayload().pipe(
    filter((payload) => !!payload),
    map((payload) => {
      const aer = (payload as AerApplicationSubmitRequestTaskPayload).aer;
      return (
        (aer.monitoringApproachEmissions.CALCULATION_PFC as CalculationOfPfcEmissions)?.sourceStreamEmissions?.map(
          (tier, index) => sourceStreamEmissionStatus('CALCULATION_PFC', payload, index),
        ) ?? []
      );
    }),
  );

  constructor(private readonly aerService: AerService) {}
}
