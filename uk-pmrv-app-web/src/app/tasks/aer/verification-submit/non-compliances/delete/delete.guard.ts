import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class DeleteGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const uncorrectedNonCompliances = (payload as AerApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.uncorrectedNonCompliances;
        const index = Number(route.paramMap.get('index'));
        return (
          (uncorrectedNonCompliances?.uncorrectedNonCompliances &&
            uncorrectedNonCompliances?.uncorrectedNonCompliances.some((item, idx) => idx === index)) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/non-compliances/list`)
        );
      }),
    );
  }
}
