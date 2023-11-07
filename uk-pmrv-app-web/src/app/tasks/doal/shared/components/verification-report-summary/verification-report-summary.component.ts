import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { DoalService } from '@tasks/doal/core/doal.service';

@Component({
  selector: 'app-verification-report-summary',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-page-heading>Upload verification report of the activity level report</app-page-heading>
      <app-verification-report-summary-template
        [verificationActivityLevelReport]="verificationActivityLevelReport$ | async"
        [document]="documentFile$ | async"
      ></app-verification-report-summary-template>
      <app-task-return-link [levelsUp]="2" [taskType]="taskType$ | async"></app-task-return-link>
    </app-doal-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationReportSummaryComponent {
  taskType$ = this.doalService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  verificationActivityLevelReport$ = this.doalService.payload$.pipe(
    map((payload) => payload.doal?.verificationReportOfTheActivityLevelReport),
  );
  documentFile$ = this.verificationActivityLevelReport$.pipe(
    map((verificationActivityLevelReport) => verificationActivityLevelReport?.document),
    map((file) => this.doalService.getDownloadUrlFile(file)),
  );

  constructor(private readonly doalService: DoalService) {}
}
