import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-pfc-verification',
  template: `
    <app-page-heading>{{ 'CALCULATION_PFC' | monitoringApproachEmissionDescription }} </app-page-heading>
    <app-pfc-group [data]="aerData$ | async"></app-pfc-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PfcVerificationComponent {
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  constructor(private readonly aerService: AerService) {}
}
