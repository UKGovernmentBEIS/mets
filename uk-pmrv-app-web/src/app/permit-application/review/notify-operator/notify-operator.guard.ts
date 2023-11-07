import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { notifyOperatorRequestTaskActionTypes } from '../../shared/utils/permit';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable()
export class NotifyOperatorGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        return (
          (state.allowedRequestTaskActions.some((action) => notifyOperatorRequestTaskActionTypes.includes(action)) &&
            state?.reviewSectionsCompleted?.determination === true) ||
          this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/review`)
        );
      }),
    );
  }
}
