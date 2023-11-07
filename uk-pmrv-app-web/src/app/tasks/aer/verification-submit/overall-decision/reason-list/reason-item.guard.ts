import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload, VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class ReasonItemGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const index = Number(route.paramMap.get('index'));
        const overallAssessmentInfo = (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport
          ?.overallAssessment as VerifiedWithCommentsOverallAssessment;

        return index <= (overallAssessmentInfo?.reasons?.length ?? 0) || this.router.parseUrl(baseUrl);
      }),
    );
  }
}
