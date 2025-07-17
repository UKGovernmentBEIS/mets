import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class PreviousYearItemGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      first(),
      map(
        (payload) =>
          ((payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.uncorrectedNonConformities
            ?.areTherePriorYearIssues &&
            Number(route.paramMap.get('index')) <=
              ((payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
                .uncorrectedNonConformities?.priorYearIssues?.length ?? 0)) ||
          this.router.parseUrl(
            `/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/non-conformities/previous-year`,
          ),
      ),
    );
  }
}
