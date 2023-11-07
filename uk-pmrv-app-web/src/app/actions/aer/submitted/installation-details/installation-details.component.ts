import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-installation-details',
  template: `
    <app-action-task header="Installation and operator details" [breadcrumb]="true">
      <app-installation-details-summary
        cssClass="summary-list--edge-border"
        [installationOperatorDetails]="installationOperatorDetails$ | async"
      ></app-installation-details-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationDetailsComponent {
  installationOperatorDetails$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.installationOperatorDetails));

  constructor(private readonly aerService: AerService) {}
}
