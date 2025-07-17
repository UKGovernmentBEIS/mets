import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

import { getPreviewDocumentsInfo3YearPeriodOffsetting } from '../../util/previewDocuments3YearPeriodOffsetting.util';

interface ViewModel {
  accountId: number;
  referenceCode: string;
  taskId: number;
}

@Component({
  selector: 'app-3year-period-offsetting-requirements-notify-operator',
  standalone: true,
  imports: [SharedModule],
  template: `
    <div class="govuk-grid-row" *ngIf="vm() as vm">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="vm.taskId"
          [accountId]="vm.accountId"
          [requestTaskActionType]="requestTaskActionType"
          confirmationMessage="3-year period offsetting requirements submitted"
          [referenceCode]="vm.referenceCode"
          [previewDocuments]="previewDocuments"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThreeYearPeriodOffsettingRequirementNotifyOperatorComponent {
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
    'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR';

  previewDocuments = getPreviewDocumentsInfo3YearPeriodOffsetting(this.requestTaskActionType);

  private readonly requestTaskItem = toSignal(this.store.pipe(requestTaskQuery.selectRequestTaskItem));

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
