import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AmendGroup } from '../shared/types/amend.global.type';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

@Injectable()
export class AmendGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const groupToAmend = route.paramMap.get('section') as AmendGroup;
    return this.store
      .isSectionForAmendWithStatus$(groupToAmend, false)
      .pipe(
        map(
          (isGroupForAmend) =>
            isGroupForAmend || this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}`),
        ),
      );
  }
}
