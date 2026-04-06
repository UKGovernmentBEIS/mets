import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, iif, Observable, of, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { AlrService } from '@tasks/alr/core';
import { submitWizardComplete } from '@tasks/alr/utils';

import {
  AccountVerificationBodyService,
  ALRAlrDataRegulatorReviewDecision,
  ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails,
  ALRApplicationAmendsSubmitRequestTaskPayload,
} from 'pmrv-api';

@Injectable()
export class AlrSendReportGuard {
  constructor(
    private readonly alrService: AlrService,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([
      this.alrService.requestAccountId$,
      this.alrService.payload$ as Observable<ALRApplicationAmendsSubmitRequestTaskPayload>,
    ]).pipe(
      switchMap(([accountId, payload]) => {
        const hasSendTo = ['verifier', 'regulator'].includes(route.queryParamMap.get('sendTo'));
        const regulatorVerificationRequired = (
          (payload.regulatorReviewGroupDecisions?.ALR as ALRAlrDataRegulatorReviewDecision)
            ?.details as ALRAlrDataRegulatorReviewOperatorAmendsNeededDecisionDetails
        )?.verificationRequired;

        return submitWizardComplete(payload)
          ? regulatorVerificationRequired === undefined
            ? iif(
                () => payload.verificationPerformed,
                of(true),
                this.accountVerificationBodyService
                  .getVerificationBodyOfAccount(accountId)
                  .pipe(
                    switchMap((vb) =>
                      !vb ? this.businessErrorService.showError(notFoundVerificationBodyError(accountId)) : of(true),
                    ),
                  ),
              )
            : regulatorVerificationRequired
              ? this.accountVerificationBodyService
                  .getVerificationBodyOfAccount(accountId)
                  .pipe(
                    switchMap((vb) =>
                      !vb ? this.businessErrorService.showError(notFoundVerificationBodyError(accountId)) : of(true),
                    ),
                  )
              : of(
                  hasSendTo
                    ? true
                    : this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/alr/submit/send-report/question`),
                )
          : of(this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/alr/submit`));
      }),
    );
  }
}
