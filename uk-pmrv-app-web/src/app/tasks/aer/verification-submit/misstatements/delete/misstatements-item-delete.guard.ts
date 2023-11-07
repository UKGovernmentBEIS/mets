import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class MisstatementsItemDeleteGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const uncorrectedMisstatements = (payload as AerApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.uncorrectedMisstatements;
        const index = Number(route.paramMap.get('index'));
        return (
          (uncorrectedMisstatements?.uncorrectedMisstatements &&
            uncorrectedMisstatements?.uncorrectedMisstatements.some((item, idx) => idx === index)) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/misstatements/list`)
        );
      }),
    );
  }
}
