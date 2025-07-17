import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { SiteVisitsService } from '@shared/components/review-groups/opinion-statement-group/services/site-visits.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SiteVisitsGuard {
  constructor(
    private readonly router: Router,
    private readonly aerService: AerService,
    private readonly siteVisitsService: SiteVisitsService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const siteVisit$ = (
      this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
    ).pipe(map((payload) => payload.verificationReport.opinionStatement.siteVisit));

    return siteVisit$.pipe(
      first(),
      map((siteVisit) => {
        const lastUrlSegment = route.url[route.url.length - 1].path;
        const matches = this.siteVisitsService.siteVisitTypeMatches(siteVisit?.siteVisitType, lastUrlSegment);
        return (
          matches ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/aer/verification-submit/opinion-statement/site-visits`,
          )
        );
      }),
    );
  }
}
