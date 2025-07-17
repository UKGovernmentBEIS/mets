import { ChangeDetectionStrategy, Component, computed, Signal, signal } from '@angular/core';

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

    return { isEditable, requestId, isSubmitted, requestTaskType };
  });

  constructor(
    readonly alrService: AlrService,
    private readonly store: CommonTasksStore,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  onSubmit() {
    const requestTaskType = this.requestTaskItem().requestTask.type;

    if (requestTaskType === 'ALR_APPLICATION_SUBMIT') {
      this.alrService.requestAccountId$
        .pipe(
          first(),
          switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
          switchMap((vb) => (vb ? of(vb) : this.businessErrorService.showError(notFoundVerificationBodyError()))),
          tap((vb) => {
            const state = this.store.getState();

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
    } else {
      this.alrService
        .postAlrSubmit('ALR_SUBMIT_VERIFICATION')
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.isSubmitted.set(true));
    }
  }
}
