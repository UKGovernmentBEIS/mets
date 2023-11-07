import { ChangeDetectionStrategy, Component } from '@angular/core';

import { Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-monitoring-plan',
  templateUrl: './monitoring-plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanComponent {
  payload$ = this.aerService.getPayload() as Observable<
    AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
  >;

  constructor(private readonly aerService: AerService) {}
}
