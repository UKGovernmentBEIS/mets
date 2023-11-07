import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationVerificationSubmittedRequestActionPayload,
} from 'pmrv-api';

import { AerService } from '../../core/aer.service';

@Component({
  selector: 'app-verified-activity-level-report',
  template: `
    <app-action-task header="Verification report of the activity level report" [breadcrumb]="true">
      <app-activity-level-report-group
        [activityLevelReport]="activityLevelReport$ | async"
        [documentFiles]="documentFiles$ | async"
      ></app-activity-level-report-group>
    </app-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerifiedActivityLevelReportComponent {
  activityLevelReport$ = (
    this.aerService.getPayload() as Observable<
      AerApplicationVerificationSubmittedRequestActionPayload | AerApplicationCompletedRequestActionPayload
    >
  ).pipe(map((payload) => payload.verificationReport?.activityLevelReport));
  documentFiles$ = this.activityLevelReport$.pipe(
    map((activityLevelReport) => activityLevelReport?.file),
    map((file) => (file ? this.aerService.getDownloadUrlFiles([file], true) : [])),
  );

  constructor(private readonly aerService: AerService) {}
}
