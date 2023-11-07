import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-non-compliances',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Uncorrected non-compliances">
      <app-non-compliances-group
        [uncorrectedNonCompliances]="uncorrectedNonCompliances$ | async"
      ></app-non-compliances-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"
      ></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonCompliancesComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  uncorrectedNonCompliances$ = (
    this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.uncorrectedNonCompliances));

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
