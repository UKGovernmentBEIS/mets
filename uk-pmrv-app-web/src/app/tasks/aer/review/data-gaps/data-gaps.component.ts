import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-data-gaps',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Methodologies to close data gaps">
      <app-data-gaps-group [dataGapsInfo]="dataGapsInfo$ | async"></app-data-gaps-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"
      ></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DataGapsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  dataGapsInfo$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.methodologiesToCloseDataGaps),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
