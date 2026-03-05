import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { RequestActionInfoDTO } from 'pmrv-api';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DoalActionService } from '../../core/doal-action.service';

@Component({
  selector: 'app-doal-operator-report',
  template: `
    <app-doal-action-task header="Upload operator activity level report" [actionType]="requestActionType$ | async">
      <app-operator-report-summary-template
        [operatorActivityLevelReport]="operatorActivityLevelReport$ | async"
        [editable]="false"
        [document]="files$ | async"></app-operator-report-summary-template>
    </app-doal-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorReportComponent {
  operatorActivityLevelReport$ = this.doalActionService
    .getProceededPayload$()
    .pipe(map((payload) => payload.doal.operatorActivityLevelReport));
  files$ = this.operatorActivityLevelReport$.pipe(
    map((operatorActivityLevelReport) =>
      operatorActivityLevelReport?.document
        ? this.doalActionService.getDownloadUrlFiles([operatorActivityLevelReport.document])[0]
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
