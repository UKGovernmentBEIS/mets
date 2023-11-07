import { ChangeDetectionStrategy, Component } from '@angular/core';

import { combineLatest, map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { getReviewStatus } from '@aviation/shared/components/vir/vir-task-list/vir-task-status.util';
import { SharedModule } from '@shared/shared.module';

import { RequestItemsService } from 'pmrv-api';

interface ViewModel {
  accountId: number;
  referenceCode: string;
  taskId: number;
  isSendReportAvailable: boolean;
  pendingRfi$: Observable<boolean>;
  isEditable: boolean;
}

@Component({
  selector: 'app-send-report',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectRequestTask),
    this.store.pipe(virQuery.selectPayload),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([requestInfo, requestTask, virPayload, isEditable]) => {
      return {
        accountId: requestInfo.accountId,
        referenceCode: requestInfo.id,
        taskId: requestTask.id,
        isSendReportAvailable: getReviewStatus(virPayload, 'sendReport') === 'not started',
        pendingRfi$: this.requestItemsService
          .getItemsByRequest(requestInfo.id)
          .pipe(map((res) => res.items.some((i) => 'AVIATION_VIR_WAIT_FOR_RFI_RESPONSE' === i.taskType))),
        isEditable: isEditable,
      };
    }),
  );

  constructor(private store: RequestTaskStore, private readonly requestItemsService: RequestItemsService) {}
}
