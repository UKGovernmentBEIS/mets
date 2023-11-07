import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-recommended-improvements',
  template: `
    <app-action-task header="Recommended improvements" [breadcrumb]="true">
      <app-recommended-improvements-group
        [recommendedImprovements]="(payload$ | async).verificationReport.recommendedImprovements"
      ></app-recommended-improvements-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
