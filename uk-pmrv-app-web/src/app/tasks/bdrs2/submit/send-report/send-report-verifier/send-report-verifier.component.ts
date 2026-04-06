import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, iif, map, of, switchMap, tap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { SharedModule } from '@shared/shared.module';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AccountVerificationBodyService,
  BDRS2ApplicationSubmitRequestTaskPayload,
  VerificationBodyNameInfoDTO,
} from 'pmrv-api';

@Component({
  selector: 'app-bdrs2-send-report-verifier',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule],
  standalone: true,
  template: `
    <app-bdrs2-task returnLink="../.." [returnLinkTitle]="returnLinkTitle" [breadcrumb]="true">
      <app-page-heading>Send report for verification</app-page-heading>
      <div class="govuk-heading-m">
        Current verifier
        <p class="govuk-body">{{ assignedVerifier$ | async }}</p>
      </div>
      <p class="govuk-body">
        By selecting ‘Confirm and send’ you confirm that the information in your report is correct to the best of your
        knowledge.
      </p>
      <div class="govuk-button-group" *ngIf="bdrs2Service.isEditable$ | async">
        <button (click)="onSubmit()" appPendingButton govukButton type="button">Confirm and send</button>
      </div>
    </app-bdrs2-task>
  `,
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2SendReportVerifierComponent {
  assignedVerifier$ = this.bdrs2Service.requestAccountId$.pipe(
    switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
    map((vb) => vb.name),
  );
  returnLinkTitle = this.bdrs2Service.title();

  constructor(
    readonly bdrs2Service: BdrS2Service,
    private readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  onSubmit() {
    let accountId: number;

    this.bdrs2Service.requestAccountId$
      .pipe(
        first(),
        switchMap((id) => {
          accountId = id;
          return this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId);
        }),
        switchMap((vb) =>
          vb ? of(vb) : this.businessErrorService.showError(notFoundVerificationBodyError(accountId)),
        ),
        tap((vb) => {
          const state = this.store.getState();

          if (
            (vb as VerificationBodyNameInfoDTO)?.id &&
            (state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload)?.[
              'verificationBodyId'
            ]
          ) {
            this.store.setState({
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...state.requestTaskItem.requestTask.payload,
                    ...((state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload)?.[
                      'verificationBodyId'
                    ] !== (vb as VerificationBodyNameInfoDTO)?.id
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
            case 'BDRS2_APPLICATION_SUBMIT':
              actionType = 'BDRS2_SUBMIT_TO_VERIFIER';
              break;
            case 'BDRS2_APPLICATION_AMENDS_SUBMIT':
              actionType = 'BDRS2_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER';
              break;
          }

          return iif(
            () => !!vb,
            this.bdrs2Service.postSubmit(actionType).pipe(map(() => (vb as VerificationBodyNameInfoDTO)?.name ?? null)),
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
