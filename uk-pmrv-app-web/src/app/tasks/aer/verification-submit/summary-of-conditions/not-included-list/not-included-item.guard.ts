import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class NotIncludedItemGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

    return this.aerService.getPayload().pipe(
      first(),
      map(
        (payload) =>
          ((payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions
            ?.changesNotIncludedInPermit &&
            Number(route.paramMap.get('index')) <=
              ((payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.summaryOfConditions
                ?.approvedChangesNotIncluded?.length ?? 0)) ||
          this.router.parseUrl(baseUrl),
      ),
    );
  }
}
