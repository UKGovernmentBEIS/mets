import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { FallbackSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Injectable()
export class DeleteGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store
      .findTask<
        FallbackSourceStreamCategoryAppliedTier[]
      >('monitoringApproaches.FALLBACK.sourceStreamCategoryAppliedTiers')
      .pipe(
        map(
          (tiers) =>
            (tiers && !!tiers[Number(route.paramMap.get('index'))]) ||
            this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/fall-back`),
        ),
      );
  }
}
