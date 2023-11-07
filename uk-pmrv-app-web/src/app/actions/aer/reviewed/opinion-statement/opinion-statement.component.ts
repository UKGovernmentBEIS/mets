import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-opinion-statement',
  template: `
    <app-action-task header="Opinion statement" [breadcrumb]="true">
      <app-opinion-statement-group [payload]="payload$ | async" [showVerifiedData]="true"></app-opinion-statement-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpinionStatementComponent {
  payload$ = (this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>).pipe(
    map((payload) => payload),
  );
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
