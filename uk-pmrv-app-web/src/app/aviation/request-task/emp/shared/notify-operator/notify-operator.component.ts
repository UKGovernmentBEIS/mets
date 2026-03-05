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
import { ReviewDeterminationStatus } from '@permit-application/review/types/review.permit.type';
import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { EmpVariationDetermination, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

import { getPreviewDocumentsInfoEmp } from '../util/previewDocumentsEmp.util';

interface ViewModel {
  taskId: number;
  accountId: number;
  confirmationMessage: string;
  referenceCode: string;
  decisionType: string;
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  isRegistryToBeNotified: boolean;
  previewDocuments: DocumentFilenameAndDocumentType[];
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
          [previewDocuments]="vm.previewDocuments"></app-notify-operator>
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
    this.store.pipe(empQuery.selectPayload),
  ]).pipe(
    map(([paramMap, taskType, requestInfo, payload]) => {
      return {
        taskId: Number(paramMap.get('taskId')),
        accountId: requestInfo.accountId,
        confirmationMessage: this.getConfirmationMessage(taskType, payload.determination?.type),
        referenceCode: variationSubmitRegulatorLedRequestTaskTypes.includes(taskType) ? requestInfo.id : null,
        decisionType: variationSubmitRegulatorLedRequestTaskTypes.includes(taskType)
          ? null
          : this.getDeterminationMap(payload.determination?.type),
        requestTaskActionType: notifyOperatorRequestTaskActionTypesMap(taskType),
        isRegistryToBeNotified: taskType === 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
        previewDocuments: getPreviewDocumentsInfoEmp(
          notifyOperatorRequestTaskActionTypesMap(taskType),
          this.getDeterminationMap(payload.determination?.type) as ReviewDeterminationStatus,
        ),
      };
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly store: RequestTaskStore,
  ) {}

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
