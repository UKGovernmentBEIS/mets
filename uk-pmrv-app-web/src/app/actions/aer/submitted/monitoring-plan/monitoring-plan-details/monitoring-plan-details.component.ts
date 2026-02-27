import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

@Component({
  selector: 'app-monitoring-plan-details',
  standalone: false,
  templateUrl: './monitoring-plan-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringPlanDetailsComponent {
  @Input() payload: AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload;
}
