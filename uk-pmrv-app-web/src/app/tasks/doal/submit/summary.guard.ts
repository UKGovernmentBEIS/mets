import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DoalTaskSectionKey } from '../core/doal-task.type';
import {
  isAdditionalDocumentsPopulated,
  isDeterminationPopulated,
  isOperatorActivityLevelReportPopulated,
  isVerificationActivityLevelReportPopulated,
} from '../submit/doal.wizard';

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

        const payload = storeState.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload;
        const sectionKey = _route.data.sectionKey as DoalTaskSectionKey;
        let baseUrl = `${state.url.slice(0, state.url.lastIndexOf('/'))}`;

        if (payload?.doalSectionsCompleted[sectionKey]) {
          return true;
        }

        let isSummaryReady: boolean;
        switch (sectionKey) {
          case 'operatorActivityLevelReport':
            isSummaryReady = isOperatorActivityLevelReportPopulated(payload.doal?.operatorActivityLevelReport);
            break;
          case 'verificationReportOfTheActivityLevelReport':
            isSummaryReady = isVerificationActivityLevelReportPopulated(
              payload.doal?.verificationReportOfTheActivityLevelReport,
            );
            break;
          case 'additionalDocuments':
            isSummaryReady = isAdditionalDocumentsPopulated(payload.doal?.additionalDocuments);
            break;
          case 'activityLevelChangeInformation': {
            isSummaryReady = !!this.router.getCurrentNavigation().extras?.state?.enableViewSummary;
            break;
          }
          case 'determination': {
            baseUrl = `${baseUrl.slice(0, baseUrl.lastIndexOf('/'))}`;
            isSummaryReady = isDeterminationPopulated(payload.doal?.determination);
            break;
          }
          default:
            isSummaryReady = false;
        }

        return isSummaryReady || this.router.parseUrl(baseUrl);
      }),
    );
  }
}
