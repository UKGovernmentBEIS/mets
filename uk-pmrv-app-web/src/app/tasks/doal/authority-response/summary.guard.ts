import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { responseWizardComplete } from '@tasks/doal/authority-response/response/response.wizard';
import { DoalAuthorityTaskSectionKey } from '@tasks/doal/core/doal-task.type';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalAuthorityResponseRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class SummaryGuard {
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

        const payload = storeState.requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload;
        const baseUrl = `${state.url.slice(0, state.url.lastIndexOf('/'))}`;
        const sectionKey = _route.data.sectionKey as DoalAuthorityTaskSectionKey;

        if (payload?.doalSectionsCompleted[sectionKey]) {
          return true;
        }

        let isSummaryReady: boolean;
        switch (sectionKey) {
          case 'dateSubmittedToAuthority':
            isSummaryReady = payload?.doalSectionsCompleted['dateSubmittedToAuthority'] === false;
            break;
          case 'authorityResponse':
            isSummaryReady = responseWizardComplete(
              payload,
              this.router.getCurrentNavigation().extras?.state?.enableViewSummary,
            );
            break;
          default:
            isSummaryReady = false;
        }

        return isSummaryReady || this.router.parseUrl(baseUrl);
      }),
    );
  }
}
