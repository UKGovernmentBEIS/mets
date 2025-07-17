import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { VirService } from '@tasks/vir/core/vir.service';
import { regulatorImprovementResponseComplete } from '@tasks/vir/review/review.wizard';

import { VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SummaryGuard {
  constructor(
    private readonly router: Router,
    private readonly virService: VirService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return combineLatest([
      this.virService.payload$ as Observable<VirApplicationReviewRequestTaskPayload>,
      this.virService.isEditable$,
    ]).pipe(
      map(([payload, isEditable]) => {
        return (
          !isEditable ||
          regulatorImprovementResponseComplete(route.paramMap.get('id'), payload?.regulatorReviewResponse) ||
          this.router.parseUrl(
            `tasks/${route.paramMap.get('taskId')}/vir/review/${route.paramMap.get(
              'id',
            )}/recommendation-response-review`,
          )
        );
      }),
    );
  }
}
