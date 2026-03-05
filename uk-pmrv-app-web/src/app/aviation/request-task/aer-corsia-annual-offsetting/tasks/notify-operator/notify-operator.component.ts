import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

import { getPreviewDocumentsInfoAnnualOffsetting } from '../../util/previewDocumentsAnnualOffsetting.util';

interface ViewModel {
  accountId: number;
  referenceCode: string;
  taskId: number;
}

@Component({
  selector: 'app-annual-offsetting-requirements-notify-operator',
  standalone: true,
  imports: [SharedModule],
  template: `
    <div class="govuk-grid-row" *ngIf="vm() as vm">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="vm.taskId"
          [accountId]="vm.accountId"
          [requestTaskActionType]="requestTaskActionType"
          confirmationMessage="Annual offsetting requirements submitted"
          [referenceCode]="vm.referenceCode"
          [previewDocuments]="previewDocuments"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnnualOffsettingRequirementNotifyOperatorComponent {
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
    'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR';
  private readonly requestTaskItem = toSignal(this.store.pipe(requestTaskQuery.selectRequestTaskItem));

  previewDocuments = getPreviewDocumentsInfoAnnualOffsetting(this.requestTaskActionType);

  vm: Signal<ViewModel> = computed(() => {
    const requestTaskItem = this.requestTaskItem();

    return {
      accountId: requestTaskItem.requestInfo.accountId,
      referenceCode: requestTaskItem.requestInfo.id,
      taskId: requestTaskItem.requestTask.id,
    };
  });

  constructor(private readonly store: RequestTaskStore) {}
}
