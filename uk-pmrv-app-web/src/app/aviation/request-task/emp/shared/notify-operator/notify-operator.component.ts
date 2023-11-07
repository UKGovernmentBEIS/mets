import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, RouterLinkWithHref } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import {
  notifyOperatorRequestTaskActionTypesMap,
  variationOperatorLedReviewRequestTaskTypes,
  variationSubmitRegulatorLedRequestTaskTypes,
} from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpVariationDetermination, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

interface ViewModel {
  taskId: number;
  accountId: number;
  confirmationMessage: string;
  referenceCode: string;
  decisionType: string;
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  isRegistryToBeNotified: boolean;
}

@Component({
  selector: 'app-emp-notify-operator',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  template: `
    <div class="govuk-grid-row" *ngIf="vm$ | async as vm">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="vm.taskId"
          [accountId]="vm.accountId"
          [confirmationMessage]="vm.confirmationMessage"
          [referenceCode]="vm.referenceCode"
          [decisionType]="vm.decisionType"
          [requestTaskActionType]="vm.requestTaskActionType"
          [isRegistryToBeNotified]="vm.isRegistryToBeNotified"
        ></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmpNotifyOperatorComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.route.paramMap,
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectRelatedActions),
    this.store.pipe(empQuery.selectPayload),
  ]).pipe(
    map(([paramMap, taskType, requestInfo, relatedActions, payload]) => {
      return {
        taskId: Number(paramMap.get('taskId')),
        accountId: requestInfo.accountId,
        confirmationMessage: this.getConfirmationMessage(taskType, payload.determination?.type),
        referenceCode: variationSubmitRegulatorLedRequestTaskTypes.includes(taskType) ? requestInfo.id : null,
        decisionType: variationSubmitRegulatorLedRequestTaskTypes.includes(taskType)
          ? null
          : this.getDeterminationMap(payload.determination?.type),
        requestTaskActionType: notifyOperatorRequestTaskActionTypesMap(taskType),
        isRegistryToBeNotified:
          relatedActions.includes('EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION') ||
          relatedActions.includes('EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION'),
      };
    }),
  );

  constructor(private readonly route: ActivatedRoute, private store: RequestTaskStore) {}

  private getConfirmationMessage(
    taskType: RequestTaskDTO['type'],
    determinationType?: EmpVariationDetermination['type'],
  ): string {
    if (variationSubmitRegulatorLedRequestTaskTypes.includes(taskType)) {
      return 'Emissions monitoring plan updated';
    } else if (variationOperatorLedReviewRequestTaskTypes.includes(taskType)) {
      return 'Variation ' + this.getDeterminationMap(determinationType);
    } else {
      return 'Application ' + this.getDeterminationMap(determinationType);
    }
  }

  private getDeterminationMap(determinationType: EmpVariationDetermination['type']): string {
    switch (determinationType) {
      case 'APPROVED':
        return 'approved';

      case 'REJECTED':
        return 'rejected';

      case 'DEEMED_WITHDRAWN':
        return 'withdrawn';
    }
  }
}
