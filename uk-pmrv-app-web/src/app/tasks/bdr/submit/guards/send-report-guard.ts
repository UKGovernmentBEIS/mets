import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { BdrService } from '@tasks/bdr/shared/services/bdr.service';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { submitWizardComplete } from '../submit.wizard';

@Injectable({
  providedIn: 'root',
})
export class SendReportGuard {
  constructor(
    private readonly bdrService: BdrService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.bdrService.getPayload().pipe(
      map((payload) => payload as BDRApplicationSubmitRequestTaskPayload),
      map((payload) => {
        const verificationPerformed = payload?.verificationPerformed;

        return !verificationPerformed && submitWizardComplete(payload)
          ? payload?.bdr?.isApplicationForFreeAllocation === false ||
            (payload as any)?.regulatorReviewGroupDecisions?.BDR?.details?.verificationRequired === false
            ? true
            : this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdr/submit/send-report/verifier`)
          : verificationPerformed && submitWizardComplete(payload)
            ? this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdr/submit/send-report/regulator`)
            : true;
      }),
    );
  }
}
