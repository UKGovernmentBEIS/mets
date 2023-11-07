import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class RegulatedActivityDeleteGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const regulatedActivities$ = (
      this.aerService.getPayload() as Observable<AerApplicationVerificationSubmitRequestTaskPayload>
    ).pipe(map((payload) => payload.verificationReport.opinionStatement.regulatedActivities));

    return regulatedActivities$.pipe(
      first(),
      map((regulatedActivities) => {
        const activityType = route.paramMap.get('activityType');
        const activityFound = regulatedActivities?.find((item) => item === activityType);
        return (
          !!activityFound ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/aer/verification-submit/opinion-statement/regulated-activities`,
          )
        );
      }),
    );
  }
}
