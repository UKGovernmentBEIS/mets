import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class IdentifiedChangesListGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = `/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/summary-of-conditions`;

    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const summaryOfConditions = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
          .summaryOfConditions;

        return (
          summaryOfConditions?.notReportedChanges?.length > 0 ||
          (summaryOfConditions?.changesIdentified &&
            this.router.parseUrl(baseUrl.concat('/identified-changes-list/0'))) ||
          (!summaryOfConditions?.changesIdentified && this.router.parseUrl(baseUrl.concat('/summary')))
        );
      }),
    );
  }
}
