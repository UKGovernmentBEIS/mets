import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

import { responseWizardComplete } from '../response/response.wizard';

@Injectable({ providedIn: 'root' })
export class AlrReponseSummaryGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        if (!storeState.isEditable) {
          return true;
        }

        const payload = storeState.requestTaskItem.requestTask.payload as ALRAuthorityResponseSubmitRequestTaskPayload;
        const baseUrl = `${state.url.slice(0, state.url.lastIndexOf('/'))}`;

        if (payload?.authorityReviewSectionsCompleted['authorityResponse']) {
          return true;
        }

        const isSummaryReady = responseWizardComplete(
          payload,
          this.router.getCurrentNavigation().extras?.state?.enableViewSummary,
        );

        return isSummaryReady || this.router.parseUrl(baseUrl);
      }),
    );
  }
}
