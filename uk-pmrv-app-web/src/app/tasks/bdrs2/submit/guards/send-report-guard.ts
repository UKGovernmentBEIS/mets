import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BdrS2Service } from '@tasks/bdrs2/core';
import { submitWizardComplete } from '@tasks/bdrs2/utils';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class Bdrs2SendReportGuard {
  constructor(
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.bdrs2Service.getPayload().pipe(
      map((payload) => payload as BDRS2ApplicationSubmitRequestTaskPayload),
      map((payload) => {
        const verificationPerformed = payload?.verificationPerformed;

        const sendToVerifierOrRegulatorCondition =
          (payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'CONTINUE_AS_HSE' ||
            payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType ===
              'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT') &&
          (!payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector ||
            (payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector &&
              !payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam &&
              payload?.bdrs2?.bdrs2Files?.file));

        const sendToVerifierCondition =
          (payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType === 'CONTINUE_AS_HSE' ||
            payload?.bdrs2?.bdrs2guardQuestions?.continueApplicationForFreeAllocationType ===
              'CONTINUE_AS_MAIN_SCHEME_PARTICIPANT') &&
          payload?.bdrs2?.bdrs2guardQuestions?.inEiteSector &&
          payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam &&
          payload?.bdrs2?.bdrs2Files?.file &&
          payload?.bdrs2?.mmpFiles?.file;

        const isCbam = payload?.bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam;
        const verificationNotRequiredFromAmends =
          (payload as any)?.regulatorReviewGroupDecisions?.BDRS2?.details?.verificationRequired === false && isCbam;

        return !verificationPerformed && submitWizardComplete(payload)
          ? sendToVerifierOrRegulatorCondition || verificationNotRequiredFromAmends
            ? true
            : sendToVerifierCondition
              ? this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdrs2/submit/send-report/verifier`)
              : this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdrs2/submit/send-report/regulator`)
          : verificationPerformed && submitWizardComplete(payload)
            ? this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdrs2/submit/send-report/regulator`)
            : false;
      }),
    );
  }
}
