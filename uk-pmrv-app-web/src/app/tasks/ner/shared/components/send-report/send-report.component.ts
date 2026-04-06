import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';

import { map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { NerService } from '@tasks/ner/core';
import { nerSendReportConfirmationTitle, nerSendReportHeader, nerShowCurrentVerifierTypes } from '@tasks/ner/utils';

import { AccountVerificationBodyService, RequestInfoDTO, RequestTaskDTO } from 'pmrv-api';

import { NerTaskComponent } from '..';

interface ViewModel {
  heading: string;
  isEditable: boolean;
  requestId: RequestInfoDTO['id'];
  isSubmitted: boolean;
  requestTaskType: RequestTaskDTO['type'];
  showCurrentVerifier: boolean;
  confirmationTitle: string;
}

@Component({
  selector: 'app-ner-send-report',
  imports: [SharedModule, NerTaskComponent],
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerSendReportComponent {
  private readonly nerService = inject(NerService);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly accountVerificationBodyService = inject(AccountVerificationBodyService);
  private readonly isEditable = this.nerService.isEditable;
  private readonly requestTaskItem = this.nerService.requestTaskItem;
  private readonly isSubmitted = signal(false);

  assignedVerifier$ = this.nerService.requestAccountId$.pipe(
    switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
    map((vb) => vb.name),
  );

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const isSubmitted = this.isSubmitted();
    const { requestTask: { type: requestTaskType } = {}, requestInfo: { id: requestId } = {} } = this.requestTaskItem();

    return {
      heading: nerSendReportHeader[requestTaskType],
      isEditable,
      requestId,
      isSubmitted,
      requestTaskType,
      showCurrentVerifier: nerShowCurrentVerifierTypes.includes(requestTaskType),
      confirmationTitle: nerSendReportConfirmationTitle[requestTaskType],
    };
  });

  onSubmit() {
    this.nerService
      .postNerSubmit()
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isSubmitted.set(true));
  }
}
