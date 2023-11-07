import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { DoalAuthorityTaskSectionKey } from '@tasks/doal/core/doal-task.type';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalAuthorityResponseRequestTaskPayload, DoalGrantAuthorityResponse } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class PreliminaryAllocationGuard {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload;
          const wizardBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/doal/authority-response/response`;
          const summaryStep = `${wizardBaseUrl}/summary`;
          const preliminaryAllocationsStep = `${wizardBaseUrl}/preliminary-allocations`;
          const index = _route.paramMap.get('index');
          const sectionKey = _route.data.sectionKey as DoalAuthorityTaskSectionKey;

          return (
            (payload?.doalSectionsCompleted[sectionKey] && this.router.parseUrl(summaryStep)) ||
            (!(payload?.doalAuthority?.authorityResponse as DoalGrantAuthorityResponse)?.preliminaryAllocations?.[
              Number(index)
            ] &&
              this.router.parseUrl(preliminaryAllocationsStep)) ||
            true
          );
        }),
      )
    );
  }
}
