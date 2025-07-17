import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { isDeterminationPopulated } from '../../doal.wizard';

@Injectable({ providedIn: 'root' })
export class ProceedAuthorityGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload;
          const taskBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/doal/submit`;
          const determinationBaseUrl = `${taskBaseUrl}/determination`;
          const proceedAuthoritySummaryUrl = `${taskBaseUrl}/determination/proceed-authority/summary`;

          const type = payload.doal?.determination?.type;

          return (
            (type !== 'PROCEED_TO_AUTHORITY' && this.router.parseUrl(determinationBaseUrl)) ||
            (isDeterminationPopulated(payload.doal?.determination) &&
              this.router.parseUrl(proceedAuthoritySummaryUrl)) ||
            true
          );
        }),
      )
    );
  }
}
