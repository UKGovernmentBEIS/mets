import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-summary-of-conditions',
  template: `
    <app-action-task header="Summary of conditions, changes, clarifications and variations" [breadcrumb]="true">
      <app-summary-of-conditions-group
        [summaryOfConditionsInfo]="
          (payload$ | async).verificationReport.summaryOfConditions
        "></app-summary-of-conditions-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOfConditionsComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
