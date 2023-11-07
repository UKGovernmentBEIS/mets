import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { BehaviorSubject, first, iif, map, of, switchMap, takeUntil, tap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AccountVerificationBodyService,
  AerApplicationSubmitRequestTaskPayload,
  VerificationBodyNameInfoDTO,
} from 'pmrv-api';

import { notFoundVerificationBodyError } from '../../../error/business-errors';

@Component({
  selector: 'app-verification',
  templateUrl: './verification.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class VerificationComponent implements OnInit {
  isSubmitted$ = new BehaviorSubject(false);
  assignedVerifier$ = this.aerService.requestAccountId$.pipe(
    switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
    map((vb) => vb.name),
  );
  confirmedAssignedVerifier$ = new BehaviorSubject(null);

  constructor(
    readonly aerService: AerService,
    private readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly backlinkService: BackLinkService,
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbs: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();
    this.isSubmitted$.pipe(takeUntil(this.destroy$)).subscribe((isSubmitted) => {
      if (isSubmitted) {
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
      }
    });
  }

  onSubmit() {
    this.aerService.requestAccountId$
      .pipe(
        first(),
        switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
        switchMap((vb) => (vb ? of(vb) : this.businessErrorService.showError(notFoundVerificationBodyError()))),
        tap((vb) => {
          if ((vb as VerificationBodyNameInfoDTO)?.id) {
            const state = this.store.getState();
            this.store.setState({
              ...state,
              requestTaskItem: {
                ...state.requestTaskItem,
                requestTask: {
                  ...state.requestTaskItem.requestTask,
                  payload: {
                    ...state.requestTaskItem.requestTask.payload,
                    ...((state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
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
            case 'AER_APPLICATION_SUBMIT':
              actionType = 'AER_REQUEST_VERIFICATION';
              break;
            case 'AER_APPLICATION_AMENDS_SUBMIT':
              actionType = 'AER_REQUEST_AMENDS_VERIFICATION';
              break;
          }

          return iif(
            () => !!vb,
            this.aerService.postSubmit(actionType).pipe(map(() => (vb as VerificationBodyNameInfoDTO)?.name ?? null)),
            of(null),
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((result) => {
        this.confirmedAssignedVerifier$.next(result);
        this.isSubmitted$.next(true);
        this.backlinkService.hide();
      });
  }
}
