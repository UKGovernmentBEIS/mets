import { ChangeDetectionStrategy, Component } from '@angular/core';

import { Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-details',
  template: `
    <app-page-heading>Installation details</app-page-heading>
    <app-installation-details-group [payload]="payload$ | async"></app-installation-details-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  payload$: Observable<AerApplicationVerificationSubmitRequestTaskPayload> = this.aerService.getPayload();

  constructor(private readonly aerService: AerService) {}
}
