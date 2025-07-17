import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { combineLatest, iif, map, Observable, of, switchMap, take, tap, withLatestFrom } from 'rxjs';

import { refreshVerificationSectionsCompletedUponSubmitToVerifier } from '@aviation/request-task/aer/ukets/tasks/send-report/send-report.utils';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';

import {
  AccountVerificationBodyService,
  AviationAerUkEtsApplicationSubmitRequestTaskPayload,
  VerificationBodyNameInfoDTO,
} from 'pmrv-api';

export interface SendReportVerificationViewModel {
  header: string;
  assignedVerifier: string;
  submitHidden: boolean;
}

@Component({
  selector: 'app-send-report-verification',
  templateUrl: './send-report-verification.component.html',
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  standalone: true,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportVerificationComponent {
  vm$: Observable<SendReportVerificationViewModel> = combineLatest([
    this.store.pipe(
      requestTaskQuery.selectRequestInfo,
      map((info) => info.accountId),
      switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
      map((vb) => vb.name),
    ),
    this.store.pipe(requestTaskQuery.selectIsEditable),
  ]).pipe(
    map(([assignedVerifier, isEditable]) => {
      return {
        header: 'Send report for verification',
        assignedVerifier,
        submitHidden: !isEditable,
      };
    }),
  );

  constructor(
    private accountVerificationBodyService: AccountVerificationBodyService,
    private businessErrorService: BusinessErrorService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    protected readonly store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
  ) {}

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
            (state.requestTaskItem.requestTask.payload as AviationAerUkEtsApplicationSubmitRequestTaskPayload)
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
                      state.requestTaskItem.requestTask.payload as AviationAerUkEtsApplicationSubmitRequestTaskPayload
                    ).verificationBodyId !== (vb as VerificationBodyNameInfoDTO)?.id
                      ? { verificationSectionsCompleted: {} }
                      : {
                          verificationSectionsCompleted: refreshVerificationSectionsCompletedUponSubmitToVerifier(
                            state.requestTaskItem.requestTask
                              .payload as AviationAerUkEtsApplicationSubmitRequestTaskPayload,
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
            case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
              actionType = 'AVIATION_AER_UKETS_REQUEST_VERIFICATION';
              break;
            case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
              actionType = 'AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION';
              break;
          }

          return iif(
            () => !!vb,
            this.store.aerDelegate
              .submitAer(actionType)
              .pipe(map(() => (vb as VerificationBodyNameInfoDTO)?.name ?? null)),
            of(null),
          );
        }),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation-verifier'], { relativeTo: this.route });
      });
  }
}
