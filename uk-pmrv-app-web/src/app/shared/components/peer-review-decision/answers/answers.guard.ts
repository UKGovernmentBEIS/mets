import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { resolveRequestType } from '../../../store-resolver/request-type.resolver';
import { resolvePeerReviewDecisionUrl } from '../peer-review-decision-type-resolver';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard {
  constructor(
    private readonly router: Router,
    private location: Location,
    private readonly sharedStore: SharedStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const requestType = resolveRequestType(this.location.path());
    const peerReviewDecisionBaseUrl = resolvePeerReviewDecisionUrl(requestType, route.paramMap.get('taskId'));

    return this.sharedStore.pipe(
      first(),
      map(
        (state) =>
          (!!state.peerReviewDecision?.type && !!state.peerReviewDecision?.notes) ||
          this.router.parseUrl(peerReviewDecisionBaseUrl),
      ),
    );
  }
}
