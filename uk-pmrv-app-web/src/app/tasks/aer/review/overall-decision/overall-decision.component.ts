import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-overall-decision',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Overall decision">
      <app-overall-decision-group [overallAssessment]="overallAssessmentInfo$ | async"></app-overall-decision-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  overallAssessmentInfo$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.overallAssessment),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}
}
