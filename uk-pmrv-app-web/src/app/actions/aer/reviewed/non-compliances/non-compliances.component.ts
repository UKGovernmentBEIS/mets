import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-non-compliances',
  template: `
    <app-action-task header="Uncorrected non-compliances" [breadcrumb]="true">
      <app-non-compliances-group
        [uncorrectedNonCompliances]="(payload$ | async).verificationReport.uncorrectedNonCompliances"
      ></app-non-compliances-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );
  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
