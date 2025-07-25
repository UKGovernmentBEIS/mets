import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationFollowUpRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class ResponseGuard {
  constructor(
    private readonly router: Router,
    private readonly store: CommonTasksStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((state) => {
          const followUpResponse = (
            state.requestTaskItem.requestTask.payload as PermitNotificationFollowUpRequestTaskPayload
          )?.followUpResponse;
          return (
            ((!!followUpResponse || !state.isEditable) &&
              this.router.parseUrl(`/tasks/${route.params['taskId']}/permit-notification/follow-up/summary`)) ||
            true
          );
        }),
      )
    );
  }
}
