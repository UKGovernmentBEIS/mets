import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-fuels',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Fuels and equipment inventory">
      <app-fuels-group [aerData]="aerData$ | async"></app-fuels-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}
}
