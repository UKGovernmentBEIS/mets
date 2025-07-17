import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-calculation-emissions',
  template: `
    <app-aer-task-review
      [breadcrumb]="true"
      [notification]="notification"
      heading="{{ this.groupKey | monitoringApproachEmissionDescription }}">
      <div [ngSwitch]="this.groupKey">
        <div *ngSwitchCase="'CALCULATION_CO2'">
          <app-calculation-emissions-group [data]="aerData$ | async"></app-calculation-emissions-group>
        </div>
        <div *ngSwitchCase="'CALCULATION_PFC'">
          <app-pfc-group [data]="aerData$ | async"></app-pfc-group>
        </div>
      </div>

      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesComponent {
  groupKey = this.route.snapshot.data.groupKey;
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  aerData$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.aer),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}
}
