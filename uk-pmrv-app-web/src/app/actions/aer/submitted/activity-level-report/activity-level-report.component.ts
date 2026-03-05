import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerApplicationCompletedRequestActionPayload, AerApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-activity-level-report',
  template: `
    <app-action-task header="Activity level report" [breadcrumb]="true">
      <app-activity-level-report-group
        [activityLevelReport]="activityLevelReport$ | async"
        [documentFiles]="documentFiles$ | async"></app-activity-level-report-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelReportComponent {
  activityLevelReport$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.aer?.activityLevelReport));
  documentFiles$ = this.activityLevelReport$.pipe(
    map((activityLevelReport) => activityLevelReport?.file),
    map((file) => (file ? this.aerService.getDownloadUrlFiles([file]) : [])),
  );

  constructor(private readonly aerService: AerService) {}
}
