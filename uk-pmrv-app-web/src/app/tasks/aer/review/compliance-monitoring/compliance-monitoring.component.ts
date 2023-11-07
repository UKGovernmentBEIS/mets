import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-compliance-monitoring',
  template: `
    <app-aer-task-review
      [breadcrumb]="true"
      [notification]="notification"
      heading="Compliance with monitoring and reporting principles"
    >
      <app-compliance-monitoring-group [compliance]="compliance$ | async"></app-compliance-monitoring-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"
      ></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceMonitoringComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  compliance$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.complianceMonitoringReporting),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
