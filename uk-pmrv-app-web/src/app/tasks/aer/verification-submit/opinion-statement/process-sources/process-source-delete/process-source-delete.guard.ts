import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class ProcessSourceDeleteGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const processSources$ = (
      this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
    ).pipe(map((payload) => payload.verificationReport.opinionStatement.processSources));

    return processSources$.pipe(
      first(),
      map((processSources) => {
        const processSource = route.paramMap.get('processSource');
        const processSourceFound = processSources?.find((item) => item === processSource);
        return (
          !!processSourceFound ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/aer/verification-submit/opinion-statement/process-sources`,
          )
        );
      }),
    );
  }
}
