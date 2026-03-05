import { ChangeDetectionStrategy, Component } from '@angular/core';

import { filter, map } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfCO2Emissions } from 'pmrv-api';

import { sourceStreamEmissionStatus } from '../../shared/components/submit/emissions-status';

@Component({
  selector: 'app-calculation-emissions',
  templateUrl: './calculation-emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculationEmissionsComponent {
  isEditable$ = this.aerService.isEditable$;
  aer$ = this.aerService.getPayload().pipe(
    filter((payload) => !!payload),
    map((payload) => (payload as AerApplicationSubmitRequestTaskPayload).aer),
  );

  sourceStreamEmissions$ = this.aer$.pipe(
    map((aer) => (aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions),
  );

  statuses$ = this.aerService.getPayload().pipe(
    filter((payload) => !!payload),
    map((payload) => {
      const aer = (payload as AerApplicationSubmitRequestTaskPayload).aer;
      return (
        (aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions?.map(
          (tier, index) => sourceStreamEmissionStatus('CALCULATION_CO2', payload, index),
        ) ?? []
      );
    }),
  );

  constructor(private readonly aerService: AerService) {}
}
