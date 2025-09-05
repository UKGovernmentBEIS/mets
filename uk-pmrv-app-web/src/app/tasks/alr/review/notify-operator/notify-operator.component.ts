import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { getAlrPreviewDocumentsInfo } from '@tasks/alr/utils';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

interface ViewModel {
  taskId: number;
  accountId: number;
  requestId: string;
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-alr-notify-operator',
  standalone: true,
  imports: [SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-two-thirds">
          <app-notify-operator
            [taskId]="vm.taskId"
            [accountId]="vm.accountId"
            confirmationMessage="Application accepted"
            [requestTaskActionType]="vm.requestTaskActionType"
            [referenceCode]="vm.requestId"
            [previewDocuments]="vm.previewDocuments"></app-notify-operator>
        </div>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrNotifyOperatorComponent {
  private readonly taskId = toSignal(this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))));
  private readonly accountId = toSignal(
    this.alrService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.accountId)),
  );
  private readonly requestId = toSignal(
    this.alrService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.id)),
  );

  private readonly payload = this.alrService.payload;

  vm: Signal<ViewModel> = computed(() => {
    const requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] = 'ALR_PROCEED_TO_AUTHORITY';
    const taskId = this.taskId();
    const accountId = this.accountId();
    const requestId = this.requestId();
    const payload = this.payload() as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;

    return {
      taskId,
      accountId,
      requestId,
      requestTaskActionType,
      previewDocuments: getAlrPreviewDocumentsInfo(
        requestTaskActionType,
        payload.regulatorReviewOutcome?.determination?.type,
      ),
    };
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly alrService: AlrService,
  ) {}
}
