import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { NerService } from '@tasks/ner/core';
import {
  getNerBobyContent,
  getNerSendReportConfirmationTitle,
  getNerSendReportHeader,
  getNerWhatHappensNextContent,
  nerShowCurrentVerifierTypes,
} from '@tasks/ner/utils';

import {
  AccountVerificationBodyService,
  NERApplicationAmendsSubmitRequestTaskPayload,
  NerApplicationSubmitRequestTaskPayload,
  NERNerDataRegulatorReviewDecision,
  RequestInfoDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { NerTaskComponent } from '..';

interface ViewModel {
  heading: string;
  isEditable: boolean;
  requestId: RequestInfoDTO['id'];
  isSubmitted: boolean;
  requestTaskType: RequestTaskDTO['type'];
  showCurrentVerifier: boolean;
  confirmationTitle: string;
  reportBody: string;
  whatHappensNextContent: string;
  regulatorVerificationRequired: boolean;
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
  private readonly payload = this.nerService.payload as Signal<NerApplicationSubmitRequestTaskPayload>;
  private readonly isEditable = this.nerService.isEditable;
  private readonly requestTaskItem = this.nerService.requestTaskItem;
  private readonly isSubmitted = signal(false);
  private readonly route = inject(ActivatedRoute);

  assignedVerifier$ = this.nerService.requestAccountId$.pipe(
    switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
    map((vb) => vb.name),
  );

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const isSubmitted = this.isSubmitted();
    const requestTaskItem = this.requestTaskItem();
    const { requestTask: { type: requestTaskType } = {}, requestInfo: { id: requestId } = {} } = requestTaskItem;
    const { verificationPerformed } = this.payload() ?? {};

    const regulatorVerificationRequired = !['verifier', 'regulator'].includes(
      this.route.snapshot.queryParamMap.get('sendTo'),
    )
      ? (
          (
            (requestTaskItem.requestTask.payload as NERApplicationAmendsSubmitRequestTaskPayload)
              .regulatorReviewGroupDecisions?.NER as NERNerDataRegulatorReviewDecision
          )?.details as any
        )?.verificationRequired
      : this.route.snapshot.queryParamMap.get('sendTo') === 'verifier';

    return {
      heading: getNerSendReportHeader(requestTaskType, verificationPerformed, regulatorVerificationRequired),
      isEditable,
      requestId,
      isSubmitted,
      requestTaskType,
      showCurrentVerifier:
        (nerShowCurrentVerifierTypes.includes(requestTaskType) && !verificationPerformed) ||
        (requestTaskType === 'NER_APPLICATION_AMENDS_SUBMIT' &&
          regulatorVerificationRequired &&
          !verificationPerformed),
      confirmationTitle: getNerSendReportConfirmationTitle(
        requestTaskType,
        verificationPerformed,
        regulatorVerificationRequired,
      ),
      reportBody: getNerBobyContent(requestTaskItem, verificationPerformed, regulatorVerificationRequired),
      whatHappensNextContent: getNerWhatHappensNextContent(requestTaskType, verificationPerformed),
      regulatorVerificationRequired,
    };
  });

  onSubmit() {
    this.nerService
      .postNerSubmit(false, this.vm().regulatorVerificationRequired)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.isSubmitted.set(true));
  }
}
