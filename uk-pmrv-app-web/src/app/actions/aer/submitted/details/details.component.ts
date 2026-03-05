import { ChangeDetectionStrategy, Component } from '@angular/core';

import { Observable } from 'rxjs';

import { AerApplicationVerificationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-details',
  template: `
    <app-action-task header="Installation details" [breadcrumb]="true">
      <app-installation-details-group [payload]="payload$ | async"></app-installation-details-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  payload$: Observable<AerApplicationVerificationSubmittedRequestActionPayload> = this.aerService.getPayload();

  constructor(private readonly aerService: AerService) {}
}
