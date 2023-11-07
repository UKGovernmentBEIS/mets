import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-doal-authority-response-notify-operator',
  template: ` <div class="govuk-grid-row">
    <div class="govuk-grid-column-two-thirds">
      <app-notify-operator
        [taskId]="taskId$ | async"
        [accountId]="accountId$ | async"
        [confirmationMessage]="'Notification sent successfully'"
        requestTaskActionType="DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION"
        [referenceCode]="requestId$ | async"
      ></app-notify-operator>
    </div>
  </div>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    private readonly route: ActivatedRoute,
    private store: CommonTasksStore,
    private readonly backLinkService: BackLinkService,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.requestTaskItem$.pipe(
    map((requestTaskItem) => requestTaskItem.requestInfo.accountId),
  );
  readonly requestId$ = this.store.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.id));

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
