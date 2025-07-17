import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-monitoring-approaches',
  template: `
    <app-action-task header="Monitoring approaches used during the reporting year" [breadcrumb]="true">
      <app-approaches-used-summary-template
        [monitoringApproaches]="monitoringApproachEmissions$ | async"></app-approaches-used-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringApproachesComponent {
  monitoringApproachEmissions$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.monitoringApproachEmissions));

  constructor(private readonly aerService: AerService) {}
}
