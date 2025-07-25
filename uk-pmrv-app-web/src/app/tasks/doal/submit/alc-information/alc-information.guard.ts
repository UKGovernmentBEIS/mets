import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DoalTaskSectionKey } from '../../core/doal-task.type';

@Injectable({ providedIn: 'root' })
export class AlcInformationGuard {
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
          const wizardBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/doal/submit/alc-information`;
          const summaryStep = `${wizardBaseUrl}/summary`;
          const sectionKey = _route.data.sectionKey as DoalTaskSectionKey;

          return (payload?.doalSectionsCompleted[sectionKey] && this.router.parseUrl(summaryStep)) || true;
        }),
      )
    );
  }
}
