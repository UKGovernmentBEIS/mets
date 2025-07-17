import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-summary-of-conditions',
  template: `
    <app-action-task header="Summary of conditions, changes, clarifications and variations" [breadcrumb]="true">
      <app-summary-of-conditions-group
        [summaryOfConditionsInfo]="summaryOfConditionsInfo$ | async"></app-summary-of-conditions-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOfConditionsComponent {
  summaryOfConditionsInfo$ = (
    this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>
  ).pipe(map((payload) => payload.verificationReport.summaryOfConditions));

  constructor(private readonly aerService: AerService) {}
}
