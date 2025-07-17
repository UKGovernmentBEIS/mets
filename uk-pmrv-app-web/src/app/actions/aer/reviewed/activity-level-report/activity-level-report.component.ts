import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-activity-level-report',
  template: `
    <app-action-task header="Activity level report" [breadcrumb]="true">
      <app-activity-level-report-group
        [activityLevelReport]="activityLevelReport$ | async"
        [documentFiles]="documentFiles$ | async"></app-activity-level-report-group>
      <app-review-group-decision-summary [decisionData]="decisionData$ | async"></app-review-group-decision-summary>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelReportComponent {
  payload$ = this.aerService.getPayload() as Observable<AerApplicationCompletedRequestActionPayload>;
  activityLevelReport$ = this.payload$.pipe(map((payload) => payload.aer?.activityLevelReport));
  documentFiles$ = this.activityLevelReport$.pipe(
    map((activityLevelReport) => activityLevelReport?.file),
    map((file) => (file ? this.aerService.getDownloadUrlFiles([file]) : [])),
  );
  decisionData$ = combineLatest([this.payload$, this.route.data]).pipe(
    map(([payload, data]) => payload.reviewGroupDecisions[data.groupKey]),
  );

  constructor(
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
  ) {}
}
