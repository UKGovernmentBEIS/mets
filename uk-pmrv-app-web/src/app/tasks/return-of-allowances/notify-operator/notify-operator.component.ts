import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { ReturnOfAllowancesService } from '../core/return-of-allowances.service';

@Component({
  selector: 'app-return-of-allowances-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          requestTaskActionType="RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION"
          [confirmationMessage]="'Notification sent successfully'"
          [referenceCode]="requestId$ | async"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    private readonly returnOfAllowancesService: ReturnOfAllowancesService,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.returnOfAllowancesService.requestTaskItem$.pipe(map((data) => data.requestInfo.accountId));
  readonly requestId$ = this.returnOfAllowancesService.requestTaskItem$.pipe(map((data) => data.requestInfo.id));

  ngOnInit(): void {
    this.route.paramMap.subscribe((paramMap) => {
      const link = ['/tasks', paramMap.get('taskId'), 'return-of-allowances', 'submit'];
      this.breadcrumbService.show([{ text: 'Return of allowances', link }]);
    });
  }
}
