import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { iif, of, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { nerWizardsCompleted } from '@tasks/ner/utils';

import {
  AccountVerificationBodyService,
  NERApplicationAmendsSubmitRequestTaskPayload,
  NERNerDataRegulatorReviewDecision,
} from 'pmrv-api';

import { NerService } from '..';

export const nerSendReportGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const nerService = inject(NerService);
  const accountVerificationBodyService = inject(AccountVerificationBodyService);
  const businessErrorService = inject(BusinessErrorService);
  const accountId = nerService.requestAccountId();
  const payload = nerService.payload();
  const hasSendTo = ['verifier', 'regulator'].includes(route.queryParamMap.get('sendTo'));
  const regulatorVerificationRequired = (
    (
      (payload as NERApplicationAmendsSubmitRequestTaskPayload)?.regulatorReviewGroupDecisions
        ?.NER as NERNerDataRegulatorReviewDecision
    )?.details as any
  )?.verificationRequired;
  const verificationPerformed = (payload as NERApplicationAmendsSubmitRequestTaskPayload)?.verificationPerformed;

  return nerWizardsCompleted(payload)
    ? regulatorVerificationRequired === undefined
      ? iif(
          () => verificationPerformed,
          of(true),
          accountVerificationBodyService
            .getVerificationBodyOfAccount(accountId)
            .pipe(
              switchMap((vb) =>
                !vb ? businessErrorService.showError(notFoundVerificationBodyError(accountId)) : of(true),
              ),
            ),
        )
      : regulatorVerificationRequired
        ? accountVerificationBodyService
            .getVerificationBodyOfAccount(accountId)
            .pipe(
              switchMap((vb) =>
                !vb ? businessErrorService.showError(notFoundVerificationBodyError(accountId)) : of(true),
              ),
            )
        : of(
            hasSendTo || verificationPerformed
              ? true
              : router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/ner/submit/send-report/question`),
          )
    : of(router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/ner/submit`));
};
