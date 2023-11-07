import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { isAllSectionsApproved } from '../aer/shared/util/aer.util';
import { RequestTaskStore } from '../store';

@Injectable({
  providedIn: 'root',
})
export class CompleteReportGuard implements CanActivate {
  constructor(private readonly store: RequestTaskStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map(
        (state) =>
          (state.requestTaskItem.allowedRequestTaskActions.includes('AVIATION_AER_UKETS_COMPLETE_REVIEW') &&
            isAllSectionsApproved(state.requestTaskItem.requestTask.payload, state.requestTaskItem.requestTask.type)) ||
          this.router.parseUrl(`/aviation/tasks/${route.paramMap.get('taskId')}`),
      ),
    );
  }
}
