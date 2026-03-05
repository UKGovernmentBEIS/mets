import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { DocumentFilenameAndDocumentType } from '@shared/interfaces/previewDocumentFilenameAndDocumentType';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { getAlrAuthorityPreviewDocumentsInfo } from '@tasks/alr/utils';

import { ALRAuthorityResponseSubmitRequestTaskPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

interface ViewModel {
  taskId: number;
  accountId: number;
  requestId: string;
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
  previewDocuments: DocumentFilenameAndDocumentType[];
}

@Component({
  selector: 'app-alr-authority-notify-operator',
  standalone: true,
  imports: [SharedModule],
  template: `
    <ng-container *ngIf="vm() as vm">
      <div class="govuk-grid-row">
        <div class="govuk-grid-column-two-thirds">
          <app-notify-operator
            [taskId]="vm.taskId"
            [accountId]="vm.accountId"
            confirmationMessage="Notification sent successfully"
            [requestTaskActionType]="vm.requestTaskActionType"
            [referenceCode]="vm.requestId"
            [previewDocuments]="vm.previewDocuments"></app-notify-operator>
        </div>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrAuthorityNotifyOperatorComponent {
  private readonly taskId = toSignal(this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))));
  private readonly accountId = toSignal(
    this.alrService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.accountId)),
  );
  private readonly requestId = toSignal(
    this.alrService.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem.requestInfo.id)),
  );

  private readonly payload = this.alrService.payload;

  vm: Signal<ViewModel> = computed(() => {
    const requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
      'ALR_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION';
    const taskId = this.taskId();
    const accountId = this.accountId();
    const requestId = this.requestId();
    const payload = this.payload() as ALRAuthorityResponseSubmitRequestTaskPayload;

    return {
      taskId,
      accountId,
      requestId,
      requestTaskActionType,
      previewDocuments: getAlrAuthorityPreviewDocumentsInfo(
        requestTaskActionType,
        payload.authorityReviewOutcome.authorityResponse.type,
      ),
    };
  });

  constructor(
    private readonly route: ActivatedRoute,
    private readonly alrService: AlrService,
  ) {}
}
