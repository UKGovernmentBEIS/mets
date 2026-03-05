import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-approaches',
  template: `
    <app-action-task
      *ngIf="payload$ | async as payload"
      header="{{ this.groupKey | monitoringApproachEmissionDescription }}"
      [breadcrumb]="true">
      <div [ngSwitch]="this.groupKey">
        <div *ngSwitchCase="'CALCULATION_CO2'">
          <app-calculation-emissions-group [data]="(payload$ | async).aer"></app-calculation-emissions-group>
        </div>
        <div *ngSwitchCase="'CALCULATION_PFC'">
          <app-pfc-group [data]="(payload$ | async).aer"></app-pfc-group>
        </div>
      </div>

      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesComponent {
  groupKey = this.route.snapshot.data.groupKey;
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload]) => payload.reviewGroupDecisions[this.groupKey]),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
