import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { isWizardCompleted } from '../dre.wizard';

@Injectable({ providedIn: 'root' })
export class DeterminationReasonGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload;
          const summary = `${state.url.slice(0, state.url.lastIndexOf('/'))}/summary`;
          return (
            ((payload?.sectionCompleted || isWizardCompleted(payload?.dre)) && this.router.parseUrl(summary)) || true
          );
        }),
      )
    );
  }
}
