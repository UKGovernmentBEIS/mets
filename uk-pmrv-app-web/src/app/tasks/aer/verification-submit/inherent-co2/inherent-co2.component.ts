import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationVerificationSubmitRequestTaskPayload, InherentCO2Emissions } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-inherent-co2',
  template: `
    <app-page-heading>{{ 'INHERENT_CO2' | monitoringApproachEmissionDescription }}</app-page-heading>
    <app-inherent-co2-group [inherentInstallations]="inherentInstallations$ | async"></app-inherent-co2-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCo2Component {
  inherentInstallations$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(
    map((payload) =>
      (
        payload.aer?.monitoringApproachEmissions?.['INHERENT_CO2'] as InherentCO2Emissions
      )?.inherentReceivingTransferringInstallations.map((item) => item.inherentReceivingTransferringInstallation),
    ),
  );

  constructor(private readonly aerService: AerService) {}
}
