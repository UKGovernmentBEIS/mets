import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { of, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { nerSubmitWizardComplete } from '@tasks/ner/utils';

import { AccountVerificationBodyService } from 'pmrv-api';

import { NerService } from '..';

export const nerSendReportGuard: CanActivateFn = (route) => {
  const router = inject(Router);
  const nerService = inject(NerService);
  const accountVerificationBodyService = inject(AccountVerificationBodyService);
  const businessErrorService = inject(BusinessErrorService);
  const accountId = nerService.requestAccountId();
  const payload = nerService.payload();

  return nerSubmitWizardComplete(payload)
    ? accountVerificationBodyService
        .getVerificationBodyOfAccount(accountId)
        .pipe(
          switchMap((vb) => (vb ? of(true) : businessErrorService.showError(notFoundVerificationBodyError(accountId)))),
        )
    : of(router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/ner/submit`));
};
