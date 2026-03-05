import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-materiality-level',
  template: `
    <app-aer-task-review
      [breadcrumb]="true"
      [notification]="notification"
      [heading]="
        (yearEqualAfter25$ | async)
          ? 'Further information of relevance to the opinion'
          : 'Materiality level and reference documents'
      ">
      <app-materiality-level-group
        [materialityLevelInfo]="materialityLevelInfo$ | async"
        [yearEqualAfter25]="yearEqualAfter25$ | async"></app-materiality-level-group>
      <app-verification-review-group-decision
        (notification)="notification = $event"></app-verification-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialityLevelComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  materialityLevelInfo$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.verificationReport.materialityLevel),
  );
  yearEqualAfter25$ = this.aerService.yearEqualAfter2025$ as Observable<boolean>;

  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}
}
