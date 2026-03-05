import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-non-conformities',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Uncorrected non-conformities">
      <app-non-conformities-group
        [uncorrectedNonConformities]="uncorrectedNonConformities$ | async"></app-non-conformities-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NonConformitiesComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  uncorrectedNonConformities$ = (
    this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>
  ).pipe(map((payload) => payload.verificationReport.uncorrectedNonConformities));

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}
}
