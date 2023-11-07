import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-activity-level-report',
  template: `
    <app-page-heading>Activity level report</app-page-heading>
    <app-activity-level-report-group
      [activityLevelReport]="activityLevelReport$ | async"
      [documentFiles]="documentFiles$ | async"
    ></app-activity-level-report-group>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelReportComponent {
  activityLevelReport$ = (
    this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
  ).pipe(map((payload) => payload.aer?.activityLevelReport));
  documentFiles$ = this.activityLevelReport$.pipe(
    map((activityLevelReport) => activityLevelReport?.file),
    map((file) => (file ? this.aerService.getDownloadUrlFiles([file]) : [])),
  );

  constructor(private readonly aerService: AerService) {}
}
