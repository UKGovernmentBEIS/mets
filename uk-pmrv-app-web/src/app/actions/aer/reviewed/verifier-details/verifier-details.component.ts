import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-verifier-details',
  standalone: false,
  template: `
    <app-action-task header="Verifier details" [breadcrumb]="true">
      <app-verifier-details-group
        [verificationReport]="(payload$ | async).verificationReport"
        [emissionsTradingScheme]="emissionsTradingScheme()"></app-verifier-details-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  readonly emissionsTradingScheme = this.aerService.getEmissionsTradingSchemeSignal();

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
