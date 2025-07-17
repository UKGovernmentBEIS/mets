import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { RequestActionInfoDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionService } from '../../core/doal-action.service';

@Component({
  selector: 'app-doal-verification-report',
  template: `
    <app-doal-action-task
      header="Upload verification report of the activity level report"
      [actionType]="requestActionType$ | async">
      <app-verification-report-summary-template
        [verificationActivityLevelReport]="verificationActivityLevelReport$ | async"
        [editable]="false"
        [document]="files$ | async"></app-verification-report-summary-template>
    </app-doal-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VerificationReportComponent {
  verificationActivityLevelReport$ = this.doalActionService
    .getProceededPayload$()
    .pipe(map((payload) => payload.doal.verificationReportOfTheActivityLevelReport));
  files$ = this.verificationActivityLevelReport$.pipe(
    map((verificationReportOfTheActivityLevelReport) =>
      verificationReportOfTheActivityLevelReport?.document
        ? this.doalActionService.getDownloadUrlFiles([verificationReportOfTheActivityLevelReport.document])[0]
        : null,
    ),
  );

  requestActionType$: Observable<RequestActionInfoDTO['type']> = this.commonActionsStore.requestAction$.pipe(
    map((ra) => ra.type),
  );

  constructor(
    private readonly doalActionService: DoalActionService,
    private readonly commonActionsStore: CommonActionsStore,
  ) {}
}
