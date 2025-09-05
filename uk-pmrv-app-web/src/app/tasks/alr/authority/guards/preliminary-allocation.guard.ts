import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRAuthorityResponseSubmitRequestTaskPayload, ALRGrantAuthorityResponse } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AlrPreliminaryAllocationGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask
            .payload as ALRAuthorityResponseSubmitRequestTaskPayload;
          const wizardBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/alr/authority/response`;
          const summaryStep = `${wizardBaseUrl}/summary`;
          const preliminaryAllocationsStep = `${wizardBaseUrl}/preliminary-allocations`;
          const index = _route.paramMap.get('index');
          const sectionKey = _route.data.sectionKey as string;

          return (
            (payload?.authorityReviewSectionsCompleted[sectionKey] && this.router.parseUrl(summaryStep)) ||
            (!(payload?.authorityReviewOutcome?.authorityResponse as ALRGrantAuthorityResponse)
              ?.preliminaryAllocations?.[Number(index)] &&
              this.router.parseUrl(preliminaryAllocationsStep)) ||
            true
          );
        }),
      )
    );
  }
}
