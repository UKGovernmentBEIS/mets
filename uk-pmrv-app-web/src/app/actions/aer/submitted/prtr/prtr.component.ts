import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-prtr',
  template: `
    <app-action-task header="Pollutant Release and Transfer Register codes (PRTR)" [breadcrumb]="true">
      <app-prtr-summary-template [activities]="activities$ | async"></app-prtr-summary-template>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PrtrComponent {
  activities$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer.pollutantRegisterActivities));

  constructor(private readonly aerService: AerService) {}
}
