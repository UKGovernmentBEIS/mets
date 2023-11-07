import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-action/emp/emp.selectors';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { requestActionQuery, RequestActionStore } from '@aviation/request-action/store';
import { empHeaderTaskMap } from '@aviation/request-task/emp/shared/util/emp.util';
import { OverallDecisionSummaryTemplateComponent } from '@aviation/shared/components/emp/overall-decision-summary-template/overall-decision-summary-template.component';

import { EmpIssuanceDetermination, RequestActionDTO } from 'pmrv-api';

interface ViewModel {
  requestActionType: RequestActionDTO['type'];
  pageHeader: string;
  data: EmpIssuanceDetermination;
}

@Component({
  selector: 'app-emp-overall-decision-action',
  standalone: true,
  imports: [CommonModule, RequestActionTaskComponent, OverallDecisionSummaryTemplateComponent],
  template: `
    <app-request-action-task
      *ngIf="vm$ | async as vm"
      [header]="vm.pageHeader"
      [requestActionType]="vm.requestActionType"
      [breadcrumb]="true"
    >
      <app-overall-decision-summary-template [data]="vm.data"></app-overall-decision-summary-template>
    </app-request-action-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  constructor(public store: RequestActionStore) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(empQuery.selectRequestActionPayload),
    this.store.pipe(requestActionQuery.selectRequestActionType),
  ]).pipe(
    map(([payload, requestActionType]) => ({
      requestActionType,
      pageHeader: empHeaderTaskMap['decision'],
      data: payload.determination,
    })),
  );
}
