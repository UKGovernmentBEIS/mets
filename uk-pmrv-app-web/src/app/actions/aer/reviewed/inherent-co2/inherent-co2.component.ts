import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, InherentCO2Emissions } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-inherent-co2',
  template: `
    <app-action-task header="{{ 'INHERENT_CO2' | monitoringApproachEmissionDescription }}" [breadcrumb]="true">
      <app-inherent-co2-group [inherentInstallations]="inherentInstallations$ | async"></app-inherent-co2-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCo2Component {
  aerPayload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  decisionData$ = combineLatest([this.aerPayload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );
  inherentInstallations$ = this.aerPayload$.pipe(
    map((payload) =>
      (
        payload.aer?.monitoringApproachEmissions?.['INHERENT_CO2'] as InherentCO2Emissions
      )?.inherentReceivingTransferringInstallations.map((item) => item.inherentReceivingTransferringInstallation),
    ),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
