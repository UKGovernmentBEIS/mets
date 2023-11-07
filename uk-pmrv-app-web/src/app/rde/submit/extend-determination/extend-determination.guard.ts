import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { RequestItemsService } from 'pmrv-api';

import { waitForRfiRdeTypes } from '../../core/rde';
import { RdeStore } from '../../store/rde.store';

@Injectable({
  providedIn: 'root',
})
export class ExtendDeterminationGuard implements CanActivate {
  constructor(
    private readonly requestItemsService: RequestItemsService,
    private readonly router: Router,
    private readonly store: RdeStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return this.store.pipe(
      first(),
      map((state) => state.requestId),
      switchMap((requestId) => this.requestItemsService.getItemsByRequest(requestId)),
      first(),
      map(
        (res) =>
          !res.items.some((i) => waitForRfiRdeTypes.includes(i.taskType)) ||
          this.router.parseUrl(
            `${
              res.items.some((i) => AVIATION_REQUEST_TYPES.includes(i.requestType)) ? '/aviation' : ''
            }/rde/${route.paramMap.get('taskId')}/not-allowed`,
          ),
      ),
    );
  }
}
