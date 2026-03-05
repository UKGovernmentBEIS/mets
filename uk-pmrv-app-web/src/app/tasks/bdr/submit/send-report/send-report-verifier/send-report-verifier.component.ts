import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, iif, map, of, switchMap, tap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { BdrTaskSharedModule } from '@tasks/bdr/shared/bdr-task-shared.module';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AccountVerificationBodyService,
  BDRApplicationSubmitRequestTaskPayload,
  VerificationBodyNameInfoDTO,
} from 'pmrv-api';

@Component({
  selector: 'app-send-report-verifier',
  template: `
    <app-bdr-task>
      <app-page-heading>Send report for verification</app-page-heading>
      <div class="govuk-heading-m">
        Current verifier
        <p class="govuk-body">{{ assignedVerifier$ | async }}</p>
      </div>
      <p class="govuk-body">
        By selecting ‘Confirm and send’ you confirm that the information in your report is correct to the best of your
        knowledge.
      </p>
      <div class="govuk-button-group" *ngIf="bdrService.isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and send</button>
      </div>
      <app-bdr-return-link returnLink="../../"></app-bdr-return-link>
    </app-bdr-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule, RouterLink],
  providers: [DestroySubject],
})
export class SendReportVerifierComponent {
  assignedVerifier$ = this.bdrService.requestAccountId$.pipe(
    switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
    map((vb) => vb.name),
  );

  constructor(
    readonly bdrService: BdrService,
    private readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  onSubmit() {
    this.bdrService.requestAccountId$
      .pipe(
        first(),
        switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
        switchMap((vb) => (vb ? of(vb) : this.businessErrorService.showError(notFoundVerificationBodyError()))),
        tap((vb) => {
          const state = this.store.getState();

          if (
            (vb as VerificationBodyNameInfoDTO)?.id &&
            (state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload)?.verificationBodyId
          ) {
            this.store.setState({
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...state.requestTaskItem.requestTask.payload,
                    ...((state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload)
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
          let actionType;

          switch (requestTaskType) {
            case 'BDR_APPLICATION_SUBMIT':
              actionType = 'BDR_SUBMIT_TO_VERIFIER';
              break;
            case 'BDR_APPLICATION_AMENDS_SUBMIT':
              actionType = 'BDR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER';
              break;
          }

          return iif(
            () => !!vb,
            this.bdrService.postSubmit(actionType).pipe(map(() => (vb as VerificationBodyNameInfoDTO)?.name ?? null)),
            of(null),
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation'], { relativeTo: this.route, queryParams: { sendTo: 'verifier' } });
      });
  }
}
