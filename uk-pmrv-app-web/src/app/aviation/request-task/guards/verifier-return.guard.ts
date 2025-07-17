import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '../store';

@Injectable({
  providedIn: 'root',
})
export class VerifierReturnGuard {
  constructor(
    private readonly store: RequestTaskStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      requestTaskQuery.selectRelatedActions,
      map((relatedActions) => {
        return (
          relatedActions.includes('AVIATION_AER_UKETS_VERIFICATION_RETURN_TO_OPERATOR') ||
          relatedActions.includes('AVIATION_AER_CORSIA_VERIFICATION_RETURN_TO_OPERATOR') ||
          this.router.parseUrl(`/aviation/tasks/${route.paramMap.get('taskId')}`)
        );
      }),
    );
  }
}
