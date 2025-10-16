import { ChangeDetectionStrategy, Component, computed, Signal, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { first, iif, map, of, switchMap, tap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { AlrService } from '@tasks/alr/core';
import { ALRReturnLinkComponent } from '@tasks/alr/shared';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AccountVerificationBodyService,
  ALRAlrDataRegulatorReviewDecision,
  ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails,
  ALRApplicationAmendsSubmitRequestTaskPayload,
  ALRApplicationSubmitRequestTaskPayload,
  RequestInfoDTO,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  VerificationBodyNameInfoDTO,
} from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  requestId: RequestInfoDTO['id'];
  isSubmitted: boolean;
  requestTaskType: RequestTaskDTO['type'];
  verificationPerformed: boolean;
  regulatorVerificationRequired: boolean;
}

@Component({
  selector: 'app-alr-send-report',
  standalone: true,
  imports: [SharedModule, ALRReturnLinkComponent],
  templateUrl: './send-report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrSendReportComponent {
  isEditable = this.alrService.isEditable;
  requestTaskItem = this.alrService.requestTaskItem;
  assignedVerifier$ = this.alrService.requestAccountId$.pipe(
    switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
    map((vb) => vb.name),
  );
  isSubmitted = signal(false);

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const isSubmitted = this.isSubmitted();
    const { requestTask: { type: requestTaskType } = {}, requestInfo: { id: requestId } = {} } = this.requestTaskItem();

    const state = this.store.getState();
    const verificationPerformed = (state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload)
      .verificationPerformed;
    const regulatorVerificationRequired = !['verifier', 'regulator'].includes(
      this.route.snapshot.queryParamMap.get('sendTo'),
    )
      ? (
          (
            (state.requestTaskItem.requestTask.payload as ALRApplicationAmendsSubmitRequestTaskPayload)
              .regulatorReviewGroupDecisions?.ALR as ALRAlrDataRegulatorReviewDecision
          )?.details as ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails
        )?.verificationRequired
      : this.route.snapshot.queryParamMap.get('sendTo') === 'verifier';

    return {
      isEditable,
      requestId,
      isSubmitted,
      requestTaskType,
      verificationPerformed,
      regulatorVerificationRequired,
    };
  });

  constructor(
    readonly alrService: AlrService,
    private readonly store: CommonTasksStore,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    const requestTaskType = this.requestTaskItem().requestTask.type;
    const state = this.store.getState();
    const verificationPerformed = (state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload)
      .verificationPerformed;
    const regulatorVerificationRequired = !['verifier', 'regulator'].includes(
      this.route.snapshot.queryParamMap.get('sendTo'),
    )
      ? (
          (
            (state.requestTaskItem.requestTask.payload as ALRApplicationAmendsSubmitRequestTaskPayload)
              .regulatorReviewGroupDecisions?.ALR as ALRAlrDataRegulatorReviewDecision
          )?.details as ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails
        )?.verificationRequired
      : this.route.snapshot.queryParamMap.get('sendTo') === 'verifier';

    if (
      (requestTaskType === 'ALR_APPLICATION_SUBMIT' && !verificationPerformed) ||
      (requestTaskType === 'ALR_APPLICATION_AMENDS_SUBMIT' && regulatorVerificationRequired && !verificationPerformed)
    ) {
      this.alrService.requestAccountId$
        .pipe(
          first(),
          switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
          switchMap((vb) => (vb ? of(vb) : this.businessErrorService.showError(notFoundVerificationBodyError()))),
          tap((vb) => {
            if (
              (vb as VerificationBodyNameInfoDTO)?.id &&
              (state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload)?.verificationBodyId
            ) {
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      ...((state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload)
                        .verificationBodyId !== (vb as VerificationBodyNameInfoDTO)?.id
                        ? { verificationSectionsCompleted: {} }
                        : {}),
                    },
                  },
                },
              });
            }
          }),
          withLatestFrom(this.store.requestTaskType$),
          switchMap(([vb, requestTaskType]) => {
            let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

            switch (requestTaskType) {
              case 'ALR_APPLICATION_SUBMIT':
                actionType = 'ALR_SUBMIT_TO_VERIFIER';
                break;

              case 'ALR_APPLICATION_AMENDS_SUBMIT':
                actionType = 'ALR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER';
                break;
            }

            return iif(
              () => !!vb,
              this.alrService
                .postAlrSubmit(actionType)
                .pipe(map(() => (vb as VerificationBodyNameInfoDTO)?.name ?? null)),
              of(null),
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.isSubmitted.set(true));
    } else if (
      (requestTaskType === 'ALR_APPLICATION_SUBMIT' && verificationPerformed) ||
      (requestTaskType === 'ALR_APPLICATION_AMENDS_SUBMIT' && (!regulatorVerificationRequired || verificationPerformed))
    ) {
      this.store.requestTaskType$
        .pipe(
          first(),
          map((requestTaskType) => {
            let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

            switch (requestTaskType) {
              case 'ALR_APPLICATION_SUBMIT':
                actionType = 'ALR_SUBMIT_TO_REGULATOR';
                break;

              case 'ALR_APPLICATION_AMENDS_SUBMIT':
                actionType = 'ALR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR';
                break;
            }

            return actionType;
          }),
          switchMap((actionType) => this.alrService.postAlrSubmit(actionType)),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.isSubmitted.set(true));
    } else {
      this.alrService
        .postAlrSubmit('ALR_SUBMIT_VERIFICATION')
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.isSubmitted.set(true));
    }
  }
}
