import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-activity-level-report',
  template: `
    <app-aer-task-review [breadcrumb]="true" [notification]="notification" heading="Activity level report">
      <app-activity-level-report-group
        [activityLevelReport]="activityLevelReport$ | async"
        [documentFiles]="documentFiles$ | async"
      ></app-activity-level-report-group>
      <app-aer-review-group-decision (notification)="notification = $event"></app-aer-review-group-decision>
    </app-aer-task-review>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelReportComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  activityLevelReport$ = (this.aerService.getPayload() as Observable<AerApplicationReviewRequestTaskPayload>).pipe(
    map((payload) => payload.aer?.activityLevelReport),
  );
  documentFiles$ = this.activityLevelReport$.pipe(
    map((activityLevelReport) => activityLevelReport?.file),
    map((file) => (file ? this.aerService.getDownloadUrlFiles([file]) : [])),
  );

  constructor(private readonly aerService: AerService, private readonly router: Router) {}
}
