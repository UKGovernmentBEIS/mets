import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-details',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Installation details">
      <app-installation-details-group [payload]="payload$ | async"></app-installation-details-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  payload$: Observable<AerApplicationReviewRequestTaskPayload> = this.aerService.getPayload();

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}
}
