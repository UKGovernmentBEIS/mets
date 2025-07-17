import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { BehaviorSubject, combineLatest, iif, map, Observable, of, switchMap, take, tap, withLatestFrom } from 'rxjs';

import { refreshVerificationSectionsCompletedUponSubmitToVerifier } from '@aviation/request-task/aer/corsia/tasks/send-report/send-report.utils';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';

import {
  AccountVerificationBodyService,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  RequestInfoDTO,
  VerificationBodyNameInfoDTO,
} from 'pmrv-api';

interface ViewModel {
  header: string;
  submittedHeader: string;
  assignedVerifier: string;
  competentAuthority: RequestInfoDTO['competentAuthority'];
  requestId: string;
  isEditable: boolean;
}

@Component({
  selector: 'app-send-report-verifier',
  templateUrl: './send-report-verifier.component.html',
  imports: [SharedModule, ReturnToLinkComponent, RouterLink],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportVerifierComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private businessErrorService = inject(BusinessErrorService);
  private accountVerificationBodyService = inject(AccountVerificationBodyService);

  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(
      requestTaskQuery.selectRequestInfo,
      switchMap((requestInfo) =>
        this.accountVerificationBodyService.getVerificationBodyOfAccount(requestInfo.accountId),
      ),
      map((vb) => vb.name),
    ),
    this.store.pipe(requestTaskQuery.selectCompetentAuthority),
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([assignedVerifier, competentAuthority, requestInfo, isEditable]) => {
      return {
        header: 'Send report for verification',
        submittedHeader: 'Report sent for verification',
        assignedVerifier: assignedVerifier,
        competentAuthority: competentAuthority,
        requestId: requestInfo.id,
        isEditable: isEditable,
      };
    }),
  );

  onSubmit() {
    this.store
      .pipe(
        take(1),
        requestTaskQuery.selectRequestInfo,
        map((info) => info.accountId),
        switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
        switchMap((vb) => (vb ? of(vb) : this.businessErrorService.showError(notFoundVerificationBodyError()))),
        tap((vb) => {
          const state = this.store.getState();

          if (
            (vb as VerificationBodyNameInfoDTO)?.id &&
            (state.requestTaskItem.requestTask.payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload)
              .verificationBodyId
          ) {
            this.store.setState({
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...state.requestTaskItem.requestTask.payload,
                    ...((
                      state.requestTaskItem.requestTask.payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload
                    ).verificationBodyId !== (vb as VerificationBodyNameInfoDTO)?.id
                      ? { verificationSectionsCompleted: {} }
                      : {
                          verificationSectionsCompleted: refreshVerificationSectionsCompletedUponSubmitToVerifier(
                            state.requestTaskItem.requestTask
                              .payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
                          ),
                        }),
                  },
                },
              },
            });
          }
        }),
        withLatestFrom(this.store.pipe(requestTaskQuery.selectRequestTaskType)),
        switchMap(([vb, requestTaskType]) => {
          let actionType;

          switch (requestTaskType) {
            case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
              actionType = 'AVIATION_AER_CORSIA_REQUEST_VERIFICATION';
              break;
            case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
              actionType = 'AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION';
              break;
          }

          return iif(
            () => !!vb,
            (this.store.aerDelegate as AerCorsiaStoreDelegate)
              .submitAer(actionType)
              .pipe(map(() => (vb as VerificationBodyNameInfoDTO)?.name ?? null)),
            of(null),
          );
        }),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => this.isSubmitted$.next(true));
  }
}
