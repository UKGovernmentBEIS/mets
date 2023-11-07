import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { reportSummaryComplete } from '@tasks/air/review/review.wizard';
import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Injectable()
export class SummaryGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly airService: AirService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([
      this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>,
      this.airService.isEditable$,
    ]).pipe(
      map(([payload, isEditable]) => {
        return (
          !isEditable ||
          reportSummaryComplete(payload) ||
          this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/air/review/provide-summary`)
        );
      }),
    );
  }
}
