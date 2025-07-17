import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { DreService } from '@tasks/dre/core/dre.service';
import { getPreviewDocumentsInfo } from '@tasks/dre/shared/previewDocumentsDre.util';

@Component({
  selector: 'app-determine-reportable-emissions-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          requestTaskActionType="DRE_SUBMIT_NOTIFY_OPERATOR"
          [confirmationMessage]="'Reportable emissions updated'"
          [referenceCode]="requestId$ | async"
          [previewDocuments]="previewDocuments"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent {
  previewDocuments = getPreviewDocumentsInfo('DRE_SUBMIT_NOTIFY_OPERATOR');

  constructor(
    private readonly dreService: DreService,
    private readonly route: ActivatedRoute,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.dreService.requestTaskItem$.pipe(map((data) => data.requestInfo.accountId));
  readonly requestId$ = this.dreService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));
}
