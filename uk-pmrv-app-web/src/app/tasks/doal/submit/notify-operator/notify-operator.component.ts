import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { CommonTasksStore } from '../../../store/common-tasks.store';

@Component({
  selector: 'app-doal-submit-notify-operator',
  template: ` <div class="govuk-grid-row">
    <div class="govuk-grid-column-two-thirds">
      <app-notify-operator
        [taskId]="taskId$ | async"
        [accountId]="accountId$ | async"
        [confirmationMessage]="'Operator has been notified'"
        requestTaskActionType="DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION"
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
