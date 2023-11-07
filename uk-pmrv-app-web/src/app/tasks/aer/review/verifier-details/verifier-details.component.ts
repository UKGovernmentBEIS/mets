import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-verifier-details',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Verifier details">
      <app-verifier-details-group [verificationReport]="verificationReportData$ | async"></app-verifier-details-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"
      ></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifierDetailsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  verificationReportData$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
