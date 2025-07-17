import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AirService } from '@tasks/air/shared/services/air.service';

import { AirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Injectable()
export class SummaryGuard {
  constructor(
    private readonly router: Router,
    private readonly airService: AirService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([
      this.airService.payload$ as Observable<AirApplicationReviewRequestTaskPayload>,
      this.airService.isEditable$,
    ]).pipe(
      map(([payload, isEditable]) => {
        const reference = route.paramMap.get('id');
        return (
          !isEditable ||
          payload?.reviewSectionsCompleted?.[reference] !== undefined ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/air/review/${reference}/improvement-response-review`,
          )
        );
      }),
    );
  }
}
