import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '../store';
import { recallFromAmendsTaskActionTypes } from '../util';

@Injectable({
  providedIn: 'root',
})
export class RecallFromAmendsGuard {
  constructor(
    private readonly store: RequestTaskStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      requestTaskQuery.selectRelatedActions,
      map((relatedActions) => {
        return (
          recallFromAmendsTaskActionTypes.some((amendsAction) => relatedActions.includes(amendsAction)) ||
          this.router.parseUrl(`/aviation/tasks/${route.paramMap.get('taskId')}`)
        );
      }),
    );
  }
}
