import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-compliance-ets',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Compliance with ETS rules">
      <app-compliance-ets-group [etsComplianceRules]="etsComplianceRules$ | async"></app-compliance-ets-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"
      ></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ComplianceEtsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  etsComplianceRules$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.etsComplianceRules),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
