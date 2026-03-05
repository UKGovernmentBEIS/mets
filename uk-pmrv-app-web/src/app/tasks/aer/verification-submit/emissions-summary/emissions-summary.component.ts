import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-emissions-summary',
  template: `
    <app-page-heading>Emissions summary</app-page-heading>
    <app-emissions-summary-group [data]="aerData$ | async"></app-emissions-summary-group>
    <app-return-link returnLink=".."></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSummaryComponent {
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  constructor(private readonly aerService: AerService) {}
}
