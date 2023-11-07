import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-emissions-summary',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Emissions summary">
      <app-emissions-summary-group [data]="aerData$ | async"></app-emissions-summary-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionsSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
