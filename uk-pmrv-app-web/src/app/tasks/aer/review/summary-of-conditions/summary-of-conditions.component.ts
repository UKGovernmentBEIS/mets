import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-summary-of-conditions',
  template: `
    <app-aer-task-review
      [breadcrumb]="true"
      [notification]="notification"
      heading="Summary of conditions, changes, clarifications and variations"
    >
      <app-summary-of-conditions-group
        [summaryOfConditionsInfo]="summaryOfConditionsInfo$ | async"
      ></app-summary-of-conditions-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"
      ></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryOfConditionsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  summaryOfConditionsInfo$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.summaryOfConditions),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
