import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class MisstatementsItemGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      first(),
      map(
        (payload) =>
          ((payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.uncorrectedMisstatements
            ?.areThereUncorrectedMisstatements &&
            Number(route.paramMap.get('index')) <=
              ((payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
                .uncorrectedMisstatements?.uncorrectedMisstatements?.length ?? 0)) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/misstatements`),
      ),
    );
  }
}
