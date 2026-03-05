import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-measurement-review',
  template: `
    <app-aer-task-review
      [breadcrumb]="true"
      [notification]="notification"
      heading="{{ taskKey | monitoringApproachEmissionDescription }}">
      <app-measurement-group [data]="aerData$ | async" [taskKey]="taskKey"></app-measurement-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MeasurementReviewComponent {
  taskKey = this.route.snapshot.data.taskKey;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload?.aer),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
