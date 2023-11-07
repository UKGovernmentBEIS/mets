import { Location } from '@angular/common';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { resolveRequestType } from '../../store-resolver/request-type.resolver';
import { StoreContextResolver } from '../../store-resolver/store-context.resolver';
import { resolvePeerReviewBaseUrl, resolveRequestTaskActionType } from './peer-review-decision-type-resolver';

@Injectable()
export class PeerReviewDecisionGuard implements CanActivate {
  constructor(
    private storeResolver: StoreContextResolver,
    private readonly router: Router,
    private readonly location: Location,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const requestType = resolveRequestType(this.location.path());
    const requestTaskType$ = this.storeResolver.getRequestTaskType(requestType);
    const requestTaskActionType$ = requestTaskType$.pipe(map((type) => resolveRequestTaskActionType(type)));
    const peerReviewDecisionBaseUrl = resolvePeerReviewBaseUrl(requestType, route.paramMap.get('taskId'));
    const allowedRequestTaskActions$ = this.storeResolver.getAllowedRequestTaskActions(requestType);

    return combineLatest([requestTaskActionType$, allowedRequestTaskActions$]).pipe(
      map(([requestTaskActionType, allowedRequestTaskActions]) => {
        return (
          allowedRequestTaskActions.includes(`${requestTaskActionType}`) ||
          this.router.parseUrl(peerReviewDecisionBaseUrl)
        );
      }),
    );
  }
}
