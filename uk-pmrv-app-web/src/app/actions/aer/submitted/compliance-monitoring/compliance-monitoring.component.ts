import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-compliance-monitoring',
  template: `
    <app-action-task header="Compliance with monitoring and reporting principles" [breadcrumb]="true">
      <app-compliance-monitoring-group [compliance]="compliance$ | async"></app-compliance-monitoring-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceMonitoringComponent {
  compliance$ = (this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>).pipe(
    map((payload) => payload.verificationReport.complianceMonitoringReporting),
  );

  constructor(private readonly aerService: AerService) {}
}
