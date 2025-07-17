import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { getPreviewDocumentsInfo } from '@tasks/permit-notification/utils/previewDocumentsNotification.util';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-permit-notification-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          [confirmationMessage]="'Task completed'"
          requestTaskActionType="PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION"
          [previewDocuments]="previewDocuments$ | async"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    private readonly route: ActivatedRoute,
    private store: CommonTasksStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.pipe(map((state) => state?.requestTaskItem?.requestInfo?.accountId));

  readonly determinationStatus$ = this.store.pipe(
    map(
      (state) =>
        (state?.requestTaskItem?.requestTask?.payload as PermitNotificationApplicationReviewRequestTaskPayload)
          ?.reviewDecision?.type,
    ),
  );

  previewDocuments$ = this.determinationStatus$.pipe(
    map((determinationStatus) => {
      return getPreviewDocumentsInfo('PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION', determinationStatus);
    }),
  );

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
