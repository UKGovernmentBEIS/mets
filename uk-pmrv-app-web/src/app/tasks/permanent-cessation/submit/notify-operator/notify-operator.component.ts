import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { getPermanentCessationPreviewDocumentsInfo } from '@tasks/permanent-cessation/utils';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

interface ViewModel {
  taskId: number;
  accountId: number;
  requestId: string;
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-permanent-cessation-notify-operator',
  standalone: true,
  imports: [SharedModule],
  template: `
    <div class="govuk-grid-row" *ngIf="vm() as vm">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="vm.taskId"
          [accountId]="vm.accountId"
          confirmationMessage="Permanent cessation notice sent to operator"
          [requestTaskActionType]="vm.requestTaskActionType"
          [referenceCode]="vm.requestId"
          [previewDocuments]="vm.previewDocuments"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermanentCessationNotifyOperatorComponent {
  private readonly taskId = toSignal(this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))));
  private readonly accountId = toSignal(
    this.store.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.accountId)),
  );

  private readonly requestId = toSignal(
    this.store.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.id)),
  );

  vm: Signal<ViewModel> = computed(() => {
    const taskId = this.taskId();
    const accountId = this.accountId();
    const requestId = this.requestId();

    return {
      taskId,
      accountId,
      requestId,
      requestTaskActionType: 'PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION',
      previewDocuments: getPermanentCessationPreviewDocumentsInfo('PERMANENT_CESSATION_APPLICATION_SUBMIT'),
    };
  });

  constructor(
    public readonly store: CommonTasksStore,
    private readonly route: ActivatedRoute,
  ) {}
}
