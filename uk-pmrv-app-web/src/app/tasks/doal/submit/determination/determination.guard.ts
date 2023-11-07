import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DoalTaskSectionKey } from '../../core/doal-task.type';
import { isDeterminationPopulated } from '../doal.wizard';
import { resolveDeterminationSectionStatus } from '../section-status';

@Injectable({ providedIn: 'root' })
export class DeterminationGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload;
          const taskBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/doal/submit`;
          const determinationProceeAuthoritySummaryBaseUrl = `${taskBaseUrl}/determination/proceed-authority/summary`;
          const determinationCloseSummaryBaseUrl = `${taskBaseUrl}/determination/close/summary`;
          const sectionKey = _route.data.sectionKey as DoalTaskSectionKey;
          const determinationStatus = resolveDeterminationSectionStatus(payload);

          return (
            (determinationStatus === 'cannot start yet' && this.router.parseUrl(taskBaseUrl)) ||
            ((payload?.doalSectionsCompleted[sectionKey] || isDeterminationPopulated(payload.doal?.determination)) &&
              this.router.parseUrl(
                payload.doal?.determination?.type === 'PROCEED_TO_AUTHORITY'
                  ? determinationProceeAuthoritySummaryBaseUrl
                  : determinationCloseSummaryBaseUrl,
              )) ||
            true
          );
        }),
      )
    );
  }
}
