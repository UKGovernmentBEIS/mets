import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-materiality-level',
  standalone: false,
  template: `
    <app-action-task
      [header]="
        (isMaterialityUpdated$ | async)
          ? 'Further information of relevance to the opinion'
          : 'Materiality level and reference documents'
      "
      [breadcrumb]="true">
      <app-materiality-level-group
        [materialityLevelInfo]="(payload$ | async).verificationReport.materialityLevel"
        [isMaterialityUpdated]="isMaterialityUpdated$ | async"></app-materiality-level-group>
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
  isMaterialityUpdated$ = this.aerService.isMaterialityUpdated$ as Observable<boolean>;

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
