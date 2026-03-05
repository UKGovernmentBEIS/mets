import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, tap } from 'rxjs';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { notifyActionTypeMap } from '@tasks/inspection/shared/inspection';
import { getInspectionPreviewDocumentsInfo } from '@tasks/inspection/shared/previewDocumentsInspection.util';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-inspection-notify-operator',
  standalone: true,
  imports: [SharedModule],
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          [confirmationMessage]="'Follow-up actions sent to operator'"
          [requestTaskActionType]="notifyActionType$ | async"
          [referenceCode]="requestId$ | async"
          [previewDocuments]="previewDocuments"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InspectionNotifyOperatorComponent {
  previewDocuments: DocumentFilenameAndDocumentType[];

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.requestTaskItem$.pipe(
    map((requestTaskItem) => requestTaskItem.requestInfo.accountId),
  );
  readonly requestId$ = this.store.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.id));

  readonly notifyActionType$ = this.store.requestTaskType$.pipe(
    tap((taskType) => (this.previewDocuments = getInspectionPreviewDocumentsInfo(notifyActionTypeMap[taskType]))),
    map((taskType) => notifyActionTypeMap[taskType]),
  );

  constructor(
    public readonly store: CommonTasksStore,
    private readonly route: ActivatedRoute,
  ) {}
}
