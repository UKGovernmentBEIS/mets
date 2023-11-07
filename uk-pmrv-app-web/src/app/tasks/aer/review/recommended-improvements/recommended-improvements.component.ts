import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-recommended-improvements',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Recommended improvements">
      <app-recommended-improvements-group [recommendedImprovements]="recommendedImprovements$ | async">
      </app-recommended-improvements-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"
      ></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecommendedImprovementsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  recommendedImprovements$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.recommendedImprovements),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
