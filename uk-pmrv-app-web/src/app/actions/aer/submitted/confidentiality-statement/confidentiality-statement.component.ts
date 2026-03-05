import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-confidentiality-statement',
  template: `
    <app-action-task header="Confidentiality statement" [breadcrumb]="true">
      <app-confidentiality-statement-summary-template
        [data]="confidentialityStatement$ | async"></app-confidentiality-statement-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfidentialityStatementComponent {
  confidentialityStatement$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.confidentialityStatement));

  constructor(private readonly aerService: AerService) {}
}
