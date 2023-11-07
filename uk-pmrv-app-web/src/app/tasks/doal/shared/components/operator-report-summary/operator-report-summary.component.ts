import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { DoalService } from '@tasks/doal/core/doal.service';

@Component({
  selector: 'app-operator-report-summary',
  template: `
    <app-doal-task [breadcrumb]="true">
      <app-page-heading>Upload operator activity level report</app-page-heading>
      <app-operator-report-summary-template
        [operatorActivityLevelReport]="operatorActivityLevelReport$ | async"
        [document]="documentFile$ | async"
      ></app-operator-report-summary-template>
      <app-task-return-link [levelsUp]="2" [taskType]="taskType$ | async"></app-task-return-link>
    </app-doal-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorReportSummaryComponent {
  taskType$ = this.doalService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  operatorActivityLevelReport$ = this.doalService.payload$.pipe(
    map((payload) => payload.doal?.operatorActivityLevelReport),
  );
  documentFile$ = this.operatorActivityLevelReport$.pipe(
    map((operatorActivityLevelReport) => operatorActivityLevelReport?.document),
    map((file) => this.doalService.getDownloadUrlFile(file)),
  );

  constructor(private readonly doalService: DoalService) {}
}
