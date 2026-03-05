import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-calculation-emissions',
  template: `
    <app-page-heading>{{ 'CALCULATION_CO2' | monitoringApproachEmissionDescription }}</app-page-heading>
    <app-calculation-emissions-group [data]="aerData$ | async"></app-calculation-emissions-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CalculationEmissionsComponent {
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  constructor(private readonly aerService: AerService) {}
}
