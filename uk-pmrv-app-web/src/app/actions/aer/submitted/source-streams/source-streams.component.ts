import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-source-streams',
  template: `
    <app-action-task header="Source streams (fuels and materials)" [breadcrumb]="true">
      <app-source-streams-summary-table
        [bottomBorder]="true"
        [data]="sourceStreams$ | async"
      ></app-source-streams-summary-table>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamsComponent {
  sourceStreams$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.sourceStreams));

  constructor(private readonly aerService: AerService) {}
}
