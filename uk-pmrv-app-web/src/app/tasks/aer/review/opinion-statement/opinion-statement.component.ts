import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-opinion-statement',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Opinion statement">
      <app-opinion-statement-group [payload]="payload$ | async" [showVerifiedData]="true"></app-opinion-statement-group>
      <app-verification-review-group-decision (notification)="notification = $event">
      </app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OpinionStatementComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
