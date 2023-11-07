import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-emissions-summary',
  template: `
    <app-action-task header="Emissions summary" [breadcrumb]="true">
      <app-emissions-summary-group [data]="aerPayload$ | async"></app-emissions-summary-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSummaryComponent {
  aerPayload$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer));

  constructor(private readonly aerService: AerService) {}
}
