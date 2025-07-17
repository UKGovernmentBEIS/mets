import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { isWizardCompleted } from '../non-compliance.wizard';

@Injectable({ providedIn: 'root' })
export class SummaryGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        const payload = storeState.requestTaskItem.requestTask
          .payload as NonComplianceApplicationSubmitRequestTaskPayload;
        const firstStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/details-of-breach`;
        return (
          !storeState.isEditable ||
          payload?.sectionCompleted ||
          isWizardCompleted(payload) ||
          this.router.parseUrl(firstStep)
        );
      }),
    );
  }
}
