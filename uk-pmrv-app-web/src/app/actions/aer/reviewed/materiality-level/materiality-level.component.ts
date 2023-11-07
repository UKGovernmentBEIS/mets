import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-materiality-level',
  template: `
    <app-action-task header="Materiality level and reference documents" [breadcrumb]="true">
      <app-materiality-level-group
        [materialityLevelInfo]="(payload$ | async).verificationReport.materialityLevel"
      ></app-materiality-level-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialityLevelComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  constructor(private readonly aerService: AerService, private readonly route: ActivatedRoute) {}
}
