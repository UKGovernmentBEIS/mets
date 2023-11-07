import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload, InherentCO2Emissions } from 'pmrv-api';

@Component({
  selector: 'app-inherent-co2',
  template: `
    <app-aer-task-review
      [breadcrumb]="true"
      [notification]="notification"
      heading="{{ 'INHERENT_CO2' | monitoringApproachEmissionDescription }}"
    >
      <app-inherent-co2-group [inherentInstallations]="inherentInstallations$ | async"></app-inherent-co2-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCo2Component {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  inherentInstallations$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) =>
      (
        payload.aer?.monitoringApproachEmissions?.['INHERENT_CO2'] as InherentCO2Emissions
      )?.inherentReceivingTransferringInstallations.map((item) => item.inherentReceivingTransferringInstallation),
    ),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
