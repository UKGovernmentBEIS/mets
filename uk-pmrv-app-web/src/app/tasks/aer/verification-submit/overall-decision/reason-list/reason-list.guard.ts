import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload, VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class ReasonListGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const verifiedWithComments = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
          .overallAssessment as VerifiedWithCommentsOverallAssessment;

        return (
          verifiedWithComments?.type === 'VERIFIED_WITH_COMMENTS' ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/overall-decision`)
        );
      }),
    );
  }
}
