import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { BdrService } from '@tasks/bdr/shared/services/bdr.service';

import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class OpinionStatementSummaryGuard {
  constructor(
    private readonly bdrService: BdrService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

    return combineLatest([this.bdrService.getPayload(), this.bdrService.isEditable$]).pipe(
      first(),
      map(
        ([payload, isEditable]) =>
          !isEditable ||
          ((payload as BDRApplicationVerificationSubmitRequestTaskPayload)?.verificationReport?.opinionStatement
            ?.opinionStatementFiles &&
            (payload as BDRApplicationVerificationSubmitRequestTaskPayload)?.verificationReport?.opinionStatement
              ?.opinionStatementFiles?.length > 0) ||
          this.router.parseUrl(baseUrl),
      ),
    );
  }
}
