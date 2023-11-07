import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class ListGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const recommendedImprovements = (payload as AerApplicationVerificationSubmitRequestTaskPayload)
          .verificationReport.recommendedImprovements;
        return (
          (recommendedImprovements?.areThereRecommendedImprovements &&
            recommendedImprovements?.recommendedImprovements?.length > 0) ||
          this.router.parseUrl(
            `/tasks/${route.paramMap.get('taskId')}/aer/verification-submit/recommended-improvements`,
          )
        );
      }),
    );
  }
}
