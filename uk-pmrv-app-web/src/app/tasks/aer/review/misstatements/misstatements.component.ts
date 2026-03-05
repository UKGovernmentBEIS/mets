import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-misstatements',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Uncorrected misstatements">
      <app-misstatements-group [uncorrectedMisstatements]="uncorrectedMisstatements$ | async"></app-misstatements-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MisstatementsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  uncorrectedMisstatements$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.uncorrectedMisstatements),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}
}
