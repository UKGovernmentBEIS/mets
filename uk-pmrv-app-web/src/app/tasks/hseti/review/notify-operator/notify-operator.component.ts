import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core';
import { getHsetiPreviewDocumentsInfo } from '@tasks/hseti/utils/previewDocumentsHseti.util';

import { HSETIApplicationRegulatorReviewSubmitRequestTaskPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

interface ViewModel {
  taskId: number;
  accountId: number;
  requestId: string;
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  previewDocuments: DocumentFilenameAndDocumentType[];
  confirmationMessage: string;
  decisionType: string;
}

@Component({
  selector: 'app-hseti-notify-operator',
  standalone: true,
  imports: [SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-two-thirds">
          <app-notify-operator
            [taskId]="vm.taskId"
            [accountId]="vm.accountId"
            [confirmationMessage]="vm.confirmationMessage"
            [requestTaskActionType]="vm.requestTaskActionType"
            [referenceCode]="vm.requestId"
            [decisionType]="vm.decisionType"
            [previewDocuments]="vm.previewDocuments"
            [allocationPeriod]="allocationPeriod()"></app-notify-operator>
        </div>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HsetiNotifyOperatorComponent {
  private readonly taskId = toSignal(this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))));
  private readonly accountId = toSignal(
    this.hsetiService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.accountId)),
  );
  private readonly requestId = toSignal(
    this.hsetiService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.id)),
  );
  allocationPeriod: Signal<string> = this.hsetiService.allocationPeriod;

  private readonly payload = this.hsetiService.payload;
  requestTaskType = this.hsetiService.requestTaskType;

  vm: Signal<ViewModel> = computed(() => {
    const requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
      'HSE_TI_REGULATOR_REVIEW_SUBMIT';
    const taskId = this.taskId();
    const accountId = this.accountId();
    const requestId = this.requestId();
    const requestTaskType = this.requestTaskType();
    const payload = this.payload() as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;

    const confirmationMessageOptions = {
      APPROVED: 'Application approved',
      REJECTED: 'Application rejected',
      WITHDRAWN: 'Application withdrawn',
      DEEMED_WITHDRAWN: 'Application deemed withdrawn',
    };

    return {
      taskId,
      accountId,
      requestId,
      requestTaskActionType,
      previewDocuments: getHsetiPreviewDocumentsInfo(requestTaskType, payload.overallDecision?.type),
      confirmationMessage: confirmationMessageOptions[payload.overallDecision?.type] || '',
      decisionType: payload.overallDecision?.type?.toLowerCase()?.replace(/_/g, ' ') || '',
    };
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly hsetiService: HseTiService,
  ) {}
}
